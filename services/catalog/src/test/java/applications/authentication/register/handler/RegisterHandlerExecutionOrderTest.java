package applications.authentication.register.handler;

import applications.authentication.common.HashingPasswordService;
import applications.authentication.domain.Credentials;
import applications.authentication.persistence.CredentialsCommandRepository;
import applications.authentication.persistence.CredentialsReadRepository;
import applications.authentication.register.command.CreateNewCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterHandlerExecutionOrderTest {

    @Mock
    CredentialsCommandRepository commandRepository;

    @Mock
    CredentialsReadRepository readRepository;

    @Mock
    HashingPasswordService hashingPasswordService;

    @InjectMocks
    RegisterHandler handler;

    @Test
    void shouldCheckEmailBeforeHashingAndPersisting() {
        CreateNewCommand command = new CreateNewCommand(
                "user@example.com",
                "StrongPassword123!"
        );

        when(readRepository.existsByEmail(command.email()))
                .thenReturn(false);
        when(hashingPasswordService.hash(command.password()))
                .thenReturn("hashed-password");

        handler.handle(command);

        InOrder order = inOrder(
                readRepository,
                hashingPasswordService,
                commandRepository
        );

        order.verify(readRepository).existsByEmail(command.email());
        order.verify(hashingPasswordService).hash(command.password());
        order.verify(commandRepository).persist(any(Credentials.class));
    }
}