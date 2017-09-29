package io.growingabit.jersey.filter;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.jersey.utils.JerseyContextUserFactory;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.DummySignupStage;
import io.growingabit.testUtils.Utils;

public class UserCreationFilterTest extends BaseGaeTest {

  private UserDao userDao;
  private String userId;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentDataSignupStage.class);
    ObjectifyService.register(InvitationCodeSignupStage.class);
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(StudentEmailSignupStage.class);

    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentEmailSignupStage.class);

    SignupStageFactory.registerMandatory(DummySignupStage.class);
    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    this.userDao = new UserDao();

    this.userId = "id";
  }

  @Test
  public void returnCurrentUserIfExists() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    final User user = new User();
    user.setId(this.userId);
    this.userDao.persist(user);

    new UserCreationFilter().filter(requestContext);

    assertThat(this.userDao.find(Key.create(User.class, this.userId))).isEqualTo(user);
  }

  @Test
  public void createUserIfNotExist() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    assertThat(this.userDao.exist(Key.create(User.class, this.userId))).isFalse();

    new UserCreationFilter().filter(requestContext);

    assertThat(this.userDao.exist(Key.create(User.class, this.userId))).isTrue();
  }

  @Test
  public void signupStageAreSetted() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    new UserCreationFilter().filter(requestContext);

    final User user = this.userDao.find(Key.create(User.class, this.userId));
    final Map<String, Ref<SignupStage>> signupStages = user.getSignupStages();

    assertThat(signupStages).hasSize(2);
  }

  @Test
  public void mandatorySignupStageAreSetted() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    new UserCreationFilter().filter(requestContext);

    final User user = this.userDao.find(Key.create(User.class, this.userId));
    final Map<String, Ref<SignupStage>> mandatorySignupStages = user.getMandatorySignupStages();

    assertThat(mandatorySignupStages).hasSize(2);
  }

  @Test
  public void setUserIntoRequestContext() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    new UserCreationFilter().filter(requestContext);

    final User createdUser = this.userDao.find(Key.create(User.class, this.userId));
    Mockito.verify(requestContext, Mockito.times(1)).setProperty(JerseyContextUserFactory.CONTEXT_USER_PROPERTY_NAME, createdUser);
  }

  @Test
  public void adSignupStageIfMissing() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    User user = new User();
    user.setId(this.userId);
    this.userDao.persist(user);

    new UserCreationFilter().filter(requestContext);

    user = this.userDao.find(Key.create(User.class, this.userId));
    final Map<String, Ref<SignupStage>> signupStages = user.getSignupStages();

    assertThat(signupStages).hasSize(2);
  }

  @Test
  public void adMandatorySignupStageIfMissing() throws IOException {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");

    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    User user = new User();
    user.setId(this.userId);
    this.userDao.persist(user);

    new UserCreationFilter().filter(requestContext);

    user = this.userDao.find(Key.create(User.class, this.userId));
    final Map<String, Ref<SignupStage>> signupStages = user.getMandatorySignupStages();

    assertThat(signupStages).hasSize(2);
  }

}
