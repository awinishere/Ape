package applications.authentication.register.handler;

import applications.authentication.common.HashingPasswordService;
import applications.authentication.domain.Credentials;
import applications.authentication.persistence.CredentialsCommandRepository;
import applications.authentication.persistence.CredentialsReadRepository;
import applications.authentication.register.command.CreateNewCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterHandlerHashingFailureTest {

    @Mock
    CredentialsCommandRepository commandRepository;

    @Mock
    CredentialsReadRepository readRepository;

    @Mock
    HashingPasswordService hashingPasswordService;

    @InjectMocks
    RegisterHandler handler;

    @Test
    void shouldNotPersistWhenHashingFails() {
        CreateNewCommand command = new CreateNewCommand(
                "user@example.com",
                "StrongPassword123!"
        );

        when(readRepository.existsByEmail(command.email()))
                .thenReturn(false);
        when(hashingPasswordService.hash(command.password()))
                .thenThrow(new IllegalStateException("Hashing failed"));

        assertThrows(
                IllegalStateException.class,
                () -> handler.handle(command)
        );

        verify(hashingPasswordService).hash(command.password());
        verify(commandRepository, never()).persist(any(Credentials.class));
    }
}