package io.growingabit.backoffice.controllers;

import com.googlecode.objectify.SaveException;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.jersey.annotations.Secured;
import io.growingabit.jersey.utils.UserRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

@Path("backoffice/invitation")
@Secured
@RolesAllowed(UserRoles.ADMIN)
public class InvitationController {

  private static final XLogger logger = XLoggerFactory.getXLogger(InvitationController.class);
  private final InvitationDao dao = new InvitationDao();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response list() {
    return Response.ok(this.dao.findAll()).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response save(final Invitation invitation) {
    try {
      this.dao.persist(invitation);
      return Response.ok(invitation).build();
    } catch (final SaveException e) {
      this.logger.error("Error saving Invitation code " + invitation.getInvitationCode(), e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }
}
