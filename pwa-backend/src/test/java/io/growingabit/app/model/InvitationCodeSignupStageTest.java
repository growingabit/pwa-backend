package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseDatastoreTest;
import org.junit.Before;
import org.junit.Test;

public class InvitationCodeSignupStageTest extends BaseDatastoreTest {

  private InvitationCodeSignupStageDao invitationCodeSignupStageDao;
  private UserDao userDao;
  private InvitationDao invitationDao;

  @Before
  public void setUp() {
    ObjectifyService.register(InvitationCodeSignupStage.class);
    ObjectifyService.register(Invitation.class);
    ObjectifyService.register(User.class);
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();
    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.invitationDao.persist(invitation);

    stage.setData(invitation);
    stage.setData(null);
    assertThat(stage.getData()).isNotNull();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.invitationCodeSignupStageDao.persist(new InvitationCodeSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new InvitationCodeSignupStage().isDone()).isFalse();
  }

}
