package applications.authentication.register.handler;

import applications.authentication.common.HashingPasswordService;
import applications.authentication.domain.Credentials;
import applications.authentication.persistence.CredentialsCommandRepository;
import applications.authentication.persistence.CredentialsReadRepository;
import applications.authentication.register.command.CreateNewCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import shared.notification.ApplicationsNotification;

@ApplicationScoped
public class RegisterHandler {

    private static final Logger log = Logger.getLogger(
            RegisterHandler.class
    );

    @Inject
    CredentialsCommandRepository commandRepository;

    @Inject
    CredentialsReadRepository readRepository;

    @Inject
    HashingPasswordService hashingPasswordService;

    @Transactional
    public ApplicationsNotification handle(CreateNewCommand command) {
        try {
            if (readRepository.existsByEmail(command.email())) {
                log.warnf("Registration rejected: email already exists: %s", command.email());
                throw new IllegalArgumentException("Email already exists");
            }

            Credentials credentials = new Credentials();
            credentials.setEmail(command.email());
            credentials.setPassword(hashingPasswordService.hash(command.password()));

            commandRepository.persist(credentials);

            return new ApplicationsNotification("Successfully added a new account.");
        } catch (RuntimeException exception) {
            log.errorf(
                    exception,
                    "Registration failed. Transaction will be rolled back. Email: %s",
                    command.email()
            );
            throw exception;
        }
    }
}