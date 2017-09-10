package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.DummySignupStage;
import io.growingabit.testUtils.Utils;
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
  private String userId;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(User.class);
    ObjectifyService.register(Invitation.class);
    ObjectifyService.register(InvitationCodeSignupStage.class);
    ObjectifyService.register(StudentDataSignupStage.class);

    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);

    SignupStageFactory.registerMandatory(DummySignupStage.class);
    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();

    this.userId = "id";
  }

  @Test
  public void returnCurrentUser() {
    final User user = new User();
    user.setId(this.userId);
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
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    assertThat(this.userDao.find(Key.create(User.class, this.userId))).isEqualTo(returnedUser);
  }

  @Test
  public void signupStageAreSetted() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final Map<String, Ref<SignupStage>> signupStages = returnedUser.getSignupStages();

    assertThat(signupStages).hasSize(2);
  }

  @Test
  public void mandatorySignupStageAreSetted() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final Map<String, Ref<SignupStage>> mandatorySignupStages = returnedUser.getMandatorySignupStages();

    assertThat(mandatorySignupStages).hasSize(2);
  }

  @Test
  public void confirmInvitationCode() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    //to create the user
    new MeController().getCurrenUserInfo(context).getEntity();

    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final Response response = new MeController().confirmInvitationCode(context, invitation.getInvitationCode());
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final InvitationCodeSignupStage savedStage = (InvitationCodeSignupStage) returnedUser.getMandatorySignupStages().values().iterator().next().get();

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test
  public void invitationCodeAlreadyUsed() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    //to create the user
    new MeController().getCurrenUserInfo(context).getEntity();

    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    invitation.setConfirmed();
    this.invitationDao.persist(invitation);

    final Response response = new MeController().confirmInvitationCode(context, invitation.getInvitationCode());
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void invitationCodeNotFound() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    //to create the user
    new MeController().getCurrenUserInfo(context).getEntity();

    final Response response = new MeController().confirmInvitationCode(context, "inexintent code");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void userNotFoundDringConfirmationCode() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().confirmInvitationCode(context, "a code");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentDataStep() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final Response response = new MeController().getCurrenUserInfo(context);
    final User user = (User) response.getEntity();

    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    final StudentDataSignupStage stage = new StudentDataSignupStage();
    stage.setData(studentData);
    new SignupStageExecutor(user).exec(stage);

    final String signupStageIndentifier = Settings.getConfig().getString(StudentDataSignupStage.class.getCanonicalName());
    final StudentDataSignupStage savedStage = (StudentDataSignupStage) user.getSignupStages().get(signupStageIndentifier).get();

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData()).isEqualTo(studentData);
  }

  @Test
  public void userNotFoundDuringStudentData() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    final Response response = new MeController().studentData(context, studentData);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentDataNull() {
    final Auth0UserProfile userProfile = new Auth0UserProfile(this.userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    //to create the user
    new MeController().getCurrenUserInfo(context).getEntity();

    final Response response = new MeController().studentData(context, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }
}
