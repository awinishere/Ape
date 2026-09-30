package applications.authentication.register.api.http;

public record CreateNewRequest(
        String email,
        String password
) {
}
