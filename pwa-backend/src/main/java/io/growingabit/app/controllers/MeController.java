package io.growingabit.app.controllers;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.SignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.annotations.Secured;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Path("/me")
@Secured
public class MeController {

  private final Logger logger = Logger.getLogger(MeController.class.getName());

  private final UserDao userDao;

  public MeController() {
    this.userDao = new UserDao();
  }

  @GET
  @Path("/")
  public Response getCurrenUserInfo(@Context final SecurityContext securityContext) {
    final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
    User user = null;
    try {
      user = this.userDao.find(Key.create(User.class, auth0User.getUserID()));
    } catch (final NotFoundException e) {
      // Maybe is better to move this piece of code out of the controller
      user = new User();
      user.setId(auth0User.getUserID());
      try {
        for (final SignupStage signupStage : SignupStageFactory.getSignupStages()) {
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

}
