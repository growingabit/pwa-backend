package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.DummySignupStage;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeControllerTest extends BaseDatastoreTest {

  private UserDao userDao;
  private InvitationDao invitationDao;
  private InvitationCodeSignupStageDao invitationCodeSignupStageDao;

  @Before
  public void setUp() {
    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();
  }


  @Test
  public void returnCurrentUser() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.registerMandatory(DummySignupStage.class);

    final User user = new User();
    user.setId("id");
    this.userDao.persist(user);
    final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    assertThat(returnedUser).isEqualTo(user);
  }

  @Test
  public void createUserIfNotExist() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.registerMandatory(DummySignupStage.class);

    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    assertThat(this.userDao.find(Key.create(User.class, "id"))).isEqualTo(returnedUser);
  }

  @Test
  public void signupStageAreSetted() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.registerMandatory(DummySignupStage.class);

    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final Map<String, Ref<SignupStage>> signupStages = returnedUser.getSignupStages();

    assertThat(signupStages).hasSize(1);

    final String stageIdentifier = Settings.getConfig().getString(DummySignupStage.class.getCanonicalName());
    assertThat(signupStages).containsKey(stageIdentifier);

    assertThat(signupStages.get(stageIdentifier).get()).isInstanceOf(DummySignupStage.class);

  }

  @Test
  public void mandatorySignupStageAreSetted() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.registerMandatory(DummySignupStage.class);

    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final Map<String, Ref<SignupStage>> mandatorySignupStages = returnedUser.getMandatorySignupStages();

    assertThat(mandatorySignupStages).hasSize(1);

    final String stageIdentifier = Settings.getConfig().getString(DummySignupStage.class.getCanonicalName());
    assertThat(mandatorySignupStages).containsKey(stageIdentifier);

    assertThat(mandatorySignupStages.get(stageIdentifier).get()).isInstanceOf(DummySignupStage.class);

  }

  @Test
  public void confirmInvitationCode() {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);

    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final User user = new User();
    user.setId("id");

    final Key<User> userKey = Key.create(user);
    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setUser(userKey);
    stage.setData(invitation);
    this.invitationCodeSignupStageDao.persist(stage);

    user.addMandatorySignupStage(stage);
    this.userDao.persist(user);

    final Response response = new MeController().confirmInvitationCode(context, invitation.getInvitationCode());
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final InvitationCodeSignupStage savedStage = (InvitationCodeSignupStage) returnedUser.getMandatorySignupStages().values().iterator().next().get();

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test
  public void invitationCodeAlreadyUsed() {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);

    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    invitation.setConfirmed();
    this.invitationDao.persist(invitation);

    final User user = new User();
    user.setId("id");

    final Key<User> userKey = Key.create(user);
    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setUser(userKey);
    stage.setData(invitation);
    this.invitationCodeSignupStageDao.persist(stage);

    user.addMandatorySignupStage(stage);
    this.userDao.persist(user);

    final Response response = new MeController().confirmInvitationCode(context, invitation.getInvitationCode());
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void invitationCodeNotFound() {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    final User user = new User();
    user.setId("id");
    this.userDao.persist(user);

    final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().confirmInvitationCode(context, "inexintent code");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void userNotFound() {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    final Auth0UserProfile userProfile = new Auth0UserProfile("id", "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().confirmInvitationCode(context, "a code");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }
}
