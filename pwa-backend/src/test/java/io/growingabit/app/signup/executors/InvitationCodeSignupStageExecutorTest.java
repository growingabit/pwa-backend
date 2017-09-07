package io.growingabit.app.signup.executors;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseDatastoreTest;
import org.junit.Before;
import org.junit.Test;

public class InvitationCodeSignupStageExecutorTest extends BaseDatastoreTest {

  private UserDao userDao;
  private InvitationDao invitationDao;
  private InvitationCodeSignupStageDao invitationCodeSignupStageDao;

  @Before
  public void setup() {
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();
  }

  @Test
  public void completeInvitationStep() {
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

    new InvitationCodeSignupStageExecutor().exec(stage, user);

    final InvitationCodeSignupStage savedStage = (InvitationCodeSignupStage) user.getMandatorySignupStages().values().iterator().next().get();

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test(expected = SignupStageExecutionException.class)
  public void notCompleteIfCodeIsUsed() {
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

    new InvitationCodeSignupStageExecutor().exec(stage, user);
  }

  @Test(expected = NullPointerException.class)
  public void stageMustBeNotNull() {
    final User user = new User();
    user.setId("id");
    this.userDao.persist(user);

    new InvitationCodeSignupStageExecutor().exec(null, user);
  }

  @Test(expected = NullPointerException.class)
  public void UserMustBeNotNull() {
    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setUser(Key.create(User.class, '3'));
    this.invitationCodeSignupStageDao.persist(stage);

    new InvitationCodeSignupStageExecutor().exec(stage, null);
  }

}
