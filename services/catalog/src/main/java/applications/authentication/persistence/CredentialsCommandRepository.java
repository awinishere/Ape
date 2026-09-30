package applications.authentication.persistence;

import applications.authentication.domain.Credentials;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CredentialsCommandRepository implements PanacheRepository<Credentials> { }
