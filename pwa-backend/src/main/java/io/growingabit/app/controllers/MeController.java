package io.growingabit.app.controllers;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.annotations.Secured;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Path("/me")
@Secured
public class MeController {

  private final Logger logger = Logger.getLogger(MeController.class.getName());
  private final Configuration config = Settings.getConfig();

  private final UserDao userDao;


  public MeController() {
    this.userDao = new UserDao();
  }

  private User getCurrentUser(final SecurityContext securityContext) {
    final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
    return this.userDao.find(Key.create(User.class, auth0User.getUserID()));
  }

  @GET
  @Path("")
  public Response getCurrenUserInfo(@Context final SecurityContext securityContext) {
    User user = null;
    try {
      user = this.getCurrentUser(securityContext);
    } catch (final NotFoundException e) {
      // Maybe is better to move this piece of code out of the controller
      final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
      user = new User();
      user.setId(auth0User.getUserID());
      final Key<User> userKey = Key.create(user);
      try {
        for (final SignupStage signupStage : SignupStageFactory.getMandatorySignupStages(userKey)) {
          user.addMandatorySignupStage(signupStage);
        }
        for (final SignupStage signupStage : SignupStageFactory.getSignupStages(userKey)) {
          user.addSignupStage(signupStage);
        }
        this.userDao.persist(user);
      } catch (final IllegalAccessException | InstantiationException ex) {
        this.logger.severe(ExceptionUtils.getStackTrace(e));
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      }
    }
    return Response.ok().entity(user).build();
  }

  @POST
  @Path("/invitationcode")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response confirmInvitationCode(@Context final SecurityContext securityContext, final String invitationCode) {
    try {
      final User user = this.getCurrentUser(securityContext);
      final InvitationDao invitationDao = new InvitationDao();
      try {
        final Invitation invitation = invitationDao.findByInvitationCode(invitationCode);
        final String signupStageKey = MeController.this.config.getString(InvitationCodeSignupStage.class.getCanonicalName());
        final InvitationCodeSignupStage signupStage = (InvitationCodeSignupStage) user.getMandatorySignupStages().get(signupStageKey).get();
        try {
          ObjectifyService.ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
              signupStage.setData(invitation);
              signupStage.exec(new SignupStageExecutor(user));
            }
          });
          return Response.ok().entity(user).build();
        } catch (final RuntimeException e) {
          if (e.getCause() instanceof SignupStageExecutionException) {
            throw e;
          } else {
            throw new SignupStageExecutionException(e);
          }
        }
      } catch (final NotFoundException e) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Invitation code not found").build();
      } catch (final SignupStageExecutionException e) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Invitation code already used").build();
      }
    } catch (final NotFoundException e) {
      // Should be handled this case?
      // I mean, every method of this controller should create the user
      // if it not exist?
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }
  }

}
