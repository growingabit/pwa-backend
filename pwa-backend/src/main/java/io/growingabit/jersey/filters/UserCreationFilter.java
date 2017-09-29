package io.growingabit.jersey.filters;

import java.io.IOException;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import io.growingabit.app.dao.GenericSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.annotations.Secured;
import io.growingabit.jersey.utils.JerseyContextUserFactory;

@Secured
@Priority(Priorities.USER)
public class UserCreationFilter implements ContainerRequestFilter {

  private final XLogger logger = XLoggerFactory.getXLogger(UserCreationFilter.class);

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Auth0UserProfile auth0User = (Auth0UserProfile) requestContext.getSecurityContext().getUserPrincipal();
    final UserDao userDao = new UserDao();
    final GenericSignupStageDao genericSignupStageDao = new GenericSignupStageDao();
    User user = null;
    try {
      user = userDao.find(Key.create(User.class, auth0User.getUserID()));
      try {
        checkUserStages(user, userDao, genericSignupStageDao);
      } catch (IllegalAccessException | InstantiationException ex) {
        this.logger.error("Error during user signup stage check, " + user.getId(), ex);
        requestContext.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    } catch (final NotFoundException e) {
      try {
        user = createUser(auth0User, userDao, genericSignupStageDao);
      } catch (IllegalAccessException | InstantiationException ex) {
        this.logger.error("Error during user creation, " + user.getId(), ex);
        requestContext.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    }
    requestContext.setProperty(JerseyContextUserFactory.CONTEXT_USER_PROPERTY_NAME, user);
  }

  private User createUser(final Auth0UserProfile profile, final UserDao userDao, final GenericSignupStageDao genericSignupStageDao) throws InstantiationException, IllegalAccessException {
    final User user = new User();
    user.setId(profile.getUserID());
    final Key<User> userKey = Key.create(user);

    final List<SignupStage> mandatoryStages = SignupStageFactory.getMandatorySignupStages(userKey);
    genericSignupStageDao.persist(mandatoryStages);
    for (final SignupStage signupStage : mandatoryStages) {
      user.addMandatorySignupStage(signupStage);
    }

    final List<SignupStage> regularStages = SignupStageFactory.getSignupStages(userKey);
    genericSignupStageDao.persist(regularStages);
    for (final SignupStage signupStage : regularStages) {
      user.addSignupStage(signupStage);
    }

    userDao.persist(user);

    return user;
  }

  private void checkUserStages(final User user, final UserDao userDao, final GenericSignupStageDao genericSignupStageDao) throws InstantiationException, IllegalAccessException {

    final Key<User> userKey = Key.create(user);
    boolean persist = false;

    final List<SignupStage> mandatoryStages = SignupStageFactory.getMandatorySignupStages(userKey);
    for (final SignupStage signupStage : mandatoryStages) {
      if (user.getStage(signupStage.getClass()) == null) {
        genericSignupStageDao.persist(signupStage);
        user.addMandatorySignupStage(signupStage);
        persist = true;
      }
    }

    final List<SignupStage> regularStages = SignupStageFactory.getSignupStages(userKey);
    for (final SignupStage signupStage : regularStages) {
      if (user.getStage(signupStage.getClass()) == null) {
        genericSignupStageDao.persist(signupStage);
        user.addSignupStage(signupStage);
        persist = true;
      }
    }

    if (persist) {
      userDao.persist(user);
    }

  }
}
