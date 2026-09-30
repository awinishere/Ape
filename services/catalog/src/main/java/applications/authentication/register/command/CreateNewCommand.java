package applications.authentication.register.command;

public record CreateNewCommand(
        String email,
        String password
) {
}
