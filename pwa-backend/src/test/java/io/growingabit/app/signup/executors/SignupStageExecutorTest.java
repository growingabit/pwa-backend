package io.growingabit.app.signup.executors;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseDatastoreTest;
import org.junit.Before;
import org.junit.Test;

public class SignupStageExecutorTest extends BaseDatastoreTest {

  private UserDao userDao;
  private InvitationDao invitationDao;
  private InvitationCodeSignupStageDao invitationCodeSignupStageDao;
  private User user;

  @Before
  public void setup() throws InstantiationException, IllegalAccessException {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);

    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();

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
    new SignupStageExecutor(this.user).exec(null);
  }

  @Test(expected = NullPointerException.class)
  public void invitationStepUserMustBeNotNull() {
    new SignupStageExecutor(null);
  }

}
