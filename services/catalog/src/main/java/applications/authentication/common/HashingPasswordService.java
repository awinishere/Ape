
package applications.authentication.common;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

@ApplicationScoped
public class HashingPasswordService {

    @ConfigProperty(name = "ape.security.argon2.iterations")
    int iterations;

    @ConfigProperty(name = "ape.security.argon2.memory")
    int memory;

    @ConfigProperty(name = "ape.security.argon2.parallelism")
    int parallelism;

    @ConfigProperty(name = "ape.security.argon2.salt-length")
    int saltLength;

    @ConfigProperty(name = "ape.security.argon2.hash-length")
    int hashLength;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String hash(String password) {
        byte[] salt = new byte[saltLength];
        RANDOM.nextBytes(salt);

        byte[] hash = generate(
                password, salt, memory, iterations, parallelism, hashLength
        );

        try {
            return "$argon2id$v=19$m=" + memory
                    + ",t=" + iterations
                    + ",p=" + parallelism
                    + "$" + Base64.toBase64String(salt)
                    + "$" + Base64.toBase64String(hash);
        } finally {
            Arrays.clear(hash);
            Arrays.clear(salt);
        }
    }

    public boolean matches(String password, String encodedHash) {
        if (password == null || encodedHash == null) {
            return false;
        }

        String[] parts = encodedHash.split("\\$", -1);

        if (parts.length != 6
                || !"argon2id".equals(parts[1])
                || !"v=19".equals(parts[2])) {
            return false;
        }

        int storedMemory;
        int storedIterations;
        int storedParallelism;

        try {
            String[] parameters = parts[3].split(",");

            if (parameters.length != 3
                    || !parameters[0].startsWith("m=")
                    || !parameters[1].startsWith("t=")
                    || !parameters[2].startsWith("p=")) {
                return false;
            }

            storedMemory = Integer.parseInt(parameters[0].substring(2));
            storedIterations = Integer.parseInt(parameters[1].substring(2));
            storedParallelism = Integer.parseInt(parameters[2].substring(2));
        } catch (RuntimeException exception) {
            return false;
        }

        if (storedMemory < 8 || storedMemory > 262144
                || storedIterations < 1 || storedIterations > 10
                || storedParallelism < 1 || storedParallelism > 4) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;

        try {
            salt = Base64.decode(parts[4]);
            expectedHash = Base64.decode(parts[5]);
        } catch (RuntimeException exception) {
            return false;
        }

        if (salt.length < 8 || salt.length > 1024
                || expectedHash.length < 16
                || expectedHash.length > 128) {
            Arrays.clear(salt);
            Arrays.clear(expectedHash);
            return false;
        }

        byte[] actualHash = generate(
                password,
                salt,
                storedMemory,
                storedIterations,
                storedParallelism,
                expectedHash.length
        );

        try {
            return MessageDigest.isEqual(expectedHash, actualHash);
        } finally {
            Arrays.clear(salt);
            Arrays.clear(expectedHash);
            Arrays.clear(actualHash);
        }
    }

    private byte[] generate(
            String password,
            byte[] salt,
            int memory,
            int iterations,
            int parallelism,
            int outputLength
    ) {
        Argon2Parameters parameters = new Argon2Parameters.Builder(
                Argon2Parameters.ARGON2_id
        )
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withMemoryAsKB(memory)
                .withIterations(iterations)
                .withParallelism(parallelism)
                .withSalt(salt)
                .build();

        byte[] passwordBytes = password.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
        );
        byte[] result = new byte[outputLength];

        try {
            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(parameters);
            generator.generateBytes(passwordBytes, result);
            return result;
        } catch (RuntimeException exception) {
            Arrays.clear(result);
            throw exception;
        } finally {
            Arrays.clear(passwordBytes);
            parameters.clear();
        }
    }
}