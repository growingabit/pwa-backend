package io.growingabit.jersey.filters;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.annotations.Secured;
import io.growingabit.jersey.utils.JerseyContextUserFactory;
import java.io.IOException;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Secured
@Priority(Priorities.USER)
public class UserCreationFilter implements ContainerRequestFilter {

  private final Logger logger = Logger.getLogger(UserCreationFilter.class.getName());

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Auth0UserProfile auth0User = (Auth0UserProfile) requestContext.getSecurityContext().getUserPrincipal();
    final UserDao userDao = new UserDao();
    User user = null;
    try {
      user = userDao.find(Key.create(User.class, auth0User.getUserID()));
    } catch (final NotFoundException e) {
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
        userDao.persist(user);
      } catch (final IllegalAccessException | InstantiationException ex) {
        this.logger.severe(ExceptionUtils.getStackTrace(ex));
        requestContext.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    }
    requestContext.setProperty(JerseyContextUserFactory.CONTEXT_USER_PROPERTY_NAME, user);
  }
}
