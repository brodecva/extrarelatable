package eu.odalic.extrarelatable.api.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Component;

/**
 * Task resource definition.
 *
 * @author Václav Brodec
 */
@Component
@Path("/control")
public final class ControlResource {

  public ControlResource() {
  }

  @PUT
  @Path("/run")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response run(final Object payload) {
	  return Response.ok().build();
  }
}
