package applications.authentication.persistence;

import applications.authentication.domain.Credentials;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CredentialsReadRepository implements PanacheRepository<Credentials> {

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}