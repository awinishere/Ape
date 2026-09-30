package applications.authentication.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TestHashingPasswordService {

    private HashingPasswordService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new HashingPasswordService();

        setField("iterations", 3);
        setField("memory", 65536);
        setField("parallelism", 1);
        setField("saltLength", 16);
        setField("hashLength", 32);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = HashingPasswordService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    void shouldHashPassword() {
        String password = "MySecurePassword123!";

        String hash = service.hash(password);

        assertNotNull(hash);
        assertFalse(hash.isBlank());
        assertNotEquals(password, hash);
    }

    @Test
    void shouldGenerateArgon2idHash() {
        String hash = service.hash("password");

        assertTrue(hash.startsWith("$argon2id$v=19$"));
    }

    @Test
    void shouldIncludeConfiguredParametersInHash() {
        String hash = service.hash("password");

        assertTrue(hash.contains("m=65536,t=3,p=1"));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String password = "MySecurePassword123!";

        String firstHash = service.hash(password);
        String secondHash = service.hash(password);

        assertNotEquals(firstHash, secondHash);
    }

    @Test
    void shouldVerifyCorrectPassword() {
        String password = "MySecurePassword123!";
        String hash = service.hash(password);

        assertTrue(service.matches(password, hash));
    }

    @Test
    void shouldRejectIncorrectPassword() {
        String hash = service.hash("CorrectPassword123!");

        assertFalse(service.matches("WrongPassword123!", hash));
    }

    @Test
    void shouldVerifyEmptyPassword() {
        String hash = service.hash("");

        assertTrue(service.matches("", hash));
    }

    @Test
    void shouldVerifyPasswordWithUnicodeCharacters() {
        String password = "pässword-こんにちは-🔐";
        String hash = service.hash(password);

        assertTrue(service.matches(password, hash));
    }

    @Test
    void shouldRejectNullPassword() {
        String hash = service.hash("password");

        assertFalse(service.matches(null, hash));
    }

    @Test
    void shouldRejectNullHash() {
        assertFalse(service.matches("password", null));
    }

    @Test
    void shouldRejectEmptyHash() {
        assertFalse(service.matches("password", ""));
    }

    @Test
    void shouldRejectMalformedHash() {
        assertFalse(service.matches("password", "not-a-valid-hash"));
    }

    @Test
    void shouldRejectHashWithWrongAlgorithm() {
        String hash = service.hash("password")
                .replace("$argon2id$", "$argon2i$");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithWrongVersion() {
        String hash = service.hash("password")
                .replace("v=19", "v=16");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithMissingParameters() {
        assertFalse(service.matches(
                "password",
                "$argon2id$v=19$m=65536,t=3,p=1$"
        ));
    }

    @Test
    void shouldRejectHashWithInvalidParameters() {
        String hash = service.hash("password")
                .replace("m=65536", "m=invalid");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithExcessiveMemory() {
        String hash = service.hash("password")
                .replace("m=65536", "m=999999999");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithExcessiveIterations() {
        String hash = service.hash("password")
                .replace("t=3", "t=999");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithExcessiveParallelism() {
        String hash = service.hash("password")
                .replace("p=1", "p=999");

        assertFalse(service.matches("password", hash));
    }

    @Test
    void shouldRejectHashWithInvalidBase64() {
        String hash = service.hash("password");
        String[] parts = hash.split("\\$");

        parts[4] = "%%%invalid%%%";

        String malformedHash = String.join("$", parts);

        assertFalse(service.matches("password", malformedHash));
    }

    @Test
    void shouldRejectHashWithShortSalt() {
        String hash = service.hash("password");
        String[] parts = hash.split("\\$");

        parts[4] = "AQID";

        String malformedHash = String.join("$", parts);

        assertFalse(service.matches("password", malformedHash));
    }

    @Test
    void shouldRejectHashWithShortOutput() {
        String hash = service.hash("password");
        String[] parts = hash.split("\\$");

        parts[5] = "AQID";

        String malformedHash = String.join("$", parts);

        assertFalse(service.matches("password", malformedHash));
    }

    @Test
    void shouldVerifyHashAfterConfigurationChanges() throws Exception {
        String password = "MySecurePassword123!";
        String hash = service.hash(password);

        setField("iterations", 5);
        setField("memory", 32768);
        setField("parallelism", 2);
        setField("saltLength", 32);
        setField("hashLength", 64);

        assertTrue(service.matches(password, hash));
    }

    @Test
    void shouldUseNewConfigurationForNewHash() throws Exception {
        setField("iterations", 4);
        setField("memory", 32768);
        setField("parallelism", 2);
        setField("saltLength", 32);
        setField("hashLength", 64);

        String hash = service.hash("password");

        assertTrue(hash.contains("m=32768,t=4,p=2"));
        assertTrue(service.matches("password", hash));
    }
}
