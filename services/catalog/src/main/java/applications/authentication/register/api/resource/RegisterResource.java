package applications.authentication.register.api.resource;

import applications.authentication.register.api.http.CreateNewRequest;
import applications.authentication.register.command.CreateNewCommand;
import applications.authentication.register.handler.RegisterHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import shared.notification.ApplicationsNotification;

@Path("/api/v1/authentication/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

    @Inject
    RegisterHandler handler;

    @POST
    public Response register(CreateNewRequest request) {
        CreateNewCommand command = new CreateNewCommand(
                request.email(),
                request.password()
        );

        ApplicationsNotification response = handler.handle(command);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}