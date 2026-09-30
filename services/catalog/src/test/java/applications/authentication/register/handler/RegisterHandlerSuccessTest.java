package applications.authentication.register.handler;

import applications.authentication.common.HashingPasswordService;
import applications.authentication.domain.Credentials;
import applications.authentication.persistence.CredentialsCommandRepository;
import applications.authentication.persistence.CredentialsReadRepository;
import applications.authentication.register.command.CreateNewCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shared.notification.ApplicationsNotification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterHandlerSuccessTest {

    @Mock
    CredentialsCommandRepository commandRepository;

    @Mock
    CredentialsReadRepository readRepository;

    @Mock
    HashingPasswordService hashingPasswordService;

    @InjectMocks
    RegisterHandler handler;

    @Test
    void shouldRegisterNewAccount() {
        CreateNewCommand command = new CreateNewCommand(
                "user@example.com",
                "StrongPassword123!"
        );

        when(readRepository.existsByEmail(command.email()))
                .thenReturn(false);

        when(hashingPasswordService.hash(command.password()))
                .thenReturn("hashed-password");

        ApplicationsNotification result = handler.handle(command);

        assertNotNull(result);

        ArgumentCaptor<Credentials> captor =
                ArgumentCaptor.forClass(Credentials.class);

        verify(commandRepository).persist(captor.capture());

        Credentials saved = captor.getValue();

        assertEquals(command.email(), saved.getEmail());
        assertEquals("hashed-password", saved.getPassword());
    }
}