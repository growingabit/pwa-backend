package io.growingabit.app.signup.executors;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.Utils;
import org.junit.Before;
import org.junit.Test;

public class SignupStageExecutorTest extends BaseDatastoreTest {

  private UserDao userDao;
  private InvitationDao invitationDao;
  private User user;

  @Before
  public void setup() throws InstantiationException, IllegalAccessException, NoSuchFieldException {

    Utils.clearSignupStageFactory();

    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    ObjectifyService.factory().register(StudentDataSignupStage.class);

    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    SignupStageFactory.register(StudentDataSignupStage.class);

    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();

    this.user = new User();
    this.user.setId("id");
    this.userDao.persist(this.user);
    final Key<User> userKey = Key.create(this.user);
    for (final SignupStage signupStage : SignupStageFactory.getMandatorySignupStages(userKey)) {
      this.user.addMandatorySignupStage(signupStage);
    }
    for (final SignupStage signupStage : SignupStageFactory.getSignupStages(userKey)) {
      this.user.addSignupStage(signupStage);
    }
    this.userDao.persist(this.user);
  }

  @Test(expected = NullPointerException.class)
  public void invitationStepUserMustBeNotNull() {
    new SignupStageExecutor(null);
  }

  @Test
  public void completeInvitationStep() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setData(invitation);
    new SignupStageExecutor(this.user).exec(stage);

    final InvitationCodeSignupStage savedStage = (InvitationCodeSignupStage) this.user.getMandatorySignupStages().values().iterator().next().get();

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test(expected = SignupStageExecutionException.class)
  public void notCompleteInvitationStepIfCodeIsUsed() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    invitation.setConfirmed();
    this.invitationDao.persist(invitation);

    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setData(invitation);
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test(expected = NullPointerException.class)
  public void invitationStepStageMustBeNotNull() {
    final InvitationCodeSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test
  public void completeStudentDataStep() {
    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    final StudentDataSignupStage stage = new StudentDataSignupStage();
    stage.setData(studentData);
    new SignupStageExecutor(this.user).exec(stage);

    final String signupStageIndentifier = Settings.getConfig().getString(StudentDataSignupStage.class.getCanonicalName());
    final StudentDataSignupStage savedStage = (StudentDataSignupStage) this.user.getSignupStages().get(signupStageIndentifier).get();

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData().getName()).isEqualTo(studentData.getName());
    assertThat(savedStage.getData().getSurname()).isEqualTo(studentData.getSurname());
    assertThat(savedStage.getData().getBirthdate()).isEqualTo(studentData.getBirthdate());
  }

  @Test(expected = NullPointerException.class)
  public void userDataStepStageMustBeNotNull() {
    final StudentDataSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

}
