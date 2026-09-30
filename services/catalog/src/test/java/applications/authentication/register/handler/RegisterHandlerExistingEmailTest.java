package applications.authentication.register.handler;

import applications.authentication.common.HashingPasswordService;
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
class RegisterHandlerExistingEmailTest {

    @Mock
    CredentialsCommandRepository commandRepository;

    @Mock
    CredentialsReadRepository readRepository;

    @Mock
    HashingPasswordService hashingPasswordService;

    @InjectMocks
    RegisterHandler handler;

    @Test
    void shouldRejectExistingEmail() {
        CreateNewCommand command = new CreateNewCommand(
                "existing@example.com",
                "StrongPassword123!"
        );

        when(readRepository.existsByEmail(command.email()))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(command)
        );

        verify(readRepository).existsByEmail(command.email());
        verifyNoInteractions(hashingPasswordService);
        verifyNoInteractions(commandRepository);
    }
}