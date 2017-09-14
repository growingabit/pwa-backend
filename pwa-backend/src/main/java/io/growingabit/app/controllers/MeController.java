package io.growingabit.app.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.annotations.Secured;

@Secured
@Path("api/v1/me")
public class MeController {

  private static final XLogger log = XLoggerFactory.getXLogger(MeController.class);

  private final UserDao userDao;

  public MeController() {
    this.userDao = new UserDao();
  }

  @GET
  @Path("")
  @Produces(MediaType.APPLICATION_JSON)
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
        log.catching(ex);
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      }
    }
    return Response.ok().entity(user).build();
  }

  @POST
  @Path("/invitationcode")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response confirmInvitationCode(@Context final SecurityContext securityContext, final Invitation i) {

    try {
      final User user = this.getCurrentUser(securityContext);
      final InvitationDao invitationDao = new InvitationDao();
      try {
        final Invitation invitation = invitationDao.findByInvitationCode(i.getInvitationCode());
        final InvitationCodeSignupStage signupStage = new InvitationCodeSignupStage();
        signupStage.setData(invitation);
        signupStage.exec(new SignupStageExecutor(user));
        return Response.ok().entity(user).build();
      } catch (final NotFoundException e) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Invitation code not found").build();
      } catch (final SignupStageExecutionException e) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
      }
    } catch (final NotFoundException e) {
      // Should be handled this case?
      // I mean, every method of this controller should create the user
      // if it not exist?
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }
  }

  @POST
  @Path("/studentdata")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response studentData(@Context final SecurityContext securityContext, final StudentData data) {
    if (data == null) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final User user = this.getCurrentUser(securityContext);
      final StudentDataSignupStage stage = new StudentDataSignupStage();
      stage.setData(data);
      stage.exec(new SignupStageExecutor(user));
      return Response.ok().entity(user).build();
    } catch (final NotFoundException e) {
      // Should be handled this case?
      // I mean, every method of this controller should create the user
      // if it not exist?
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/studentemail")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response studentemail(@Context final SecurityContext securityContext, final StudentConfirmationEmail studentConfirmationEmail) {

    if (studentConfirmationEmail == null || studentConfirmationEmail.getEmail() == null) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final User user = this.getCurrentUser(securityContext);
      StudentEmailSignupStage stage = new StudentEmailSignupStage();
      stage.setData(new StudentConfirmationEmail(studentConfirmationEmail.getEmail()));
      stage.exec(new SignupStageExecutor(user));

      return Response.ok().entity(user).build();

    } catch (final NotFoundException e) {
      // Should be handled this case?
      // I mean, every method of this controller should create the user
      // if it not exist?
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    } catch (final SignupStageExecutionException | IllegalArgumentException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }

  }

  private User getCurrentUser(final SecurityContext securityContext) {
    final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
    return this.userDao.find(Key.create(User.class, auth0User.getUserID()));
  }



}
