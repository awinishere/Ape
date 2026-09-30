package applications.authentication.register.api.http;

import java.util.UUID;

public record CreateNewResponse(
        UUID id,
        String email,
        String role
) {
}
