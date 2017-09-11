package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseDatastoreTest;
import java.util.Random;
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

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.invitationCodeSignupStageDao.persist(new InvitationCodeSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new InvitationCodeSignupStage().isDone()).isFalse();
  }

  @Test
  public void equalsAndHashCode() {

    final int n = new Random().nextInt(10) + 1;

    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.invitationDao.persist(invitation);
    final InvitationCodeSignupStage dummySignupStage1 = new InvitationCodeSignupStage();
    dummySignupStage1.setData(invitation);
    final InvitationCodeSignupStage dummySignupStage2 = new InvitationCodeSignupStage();
    dummySignupStage2.setData(invitation);

    final Invitation invitation2 = new Invitation("My school2", "My class2", "This Year2", "My Spec2");
    this.invitationDao.persist(invitation2);
    final InvitationCodeSignupStage dummySignupStage3 = new InvitationCodeSignupStage();
    dummySignupStage3.setData(invitation2);
    final InvitationCodeSignupStage dummySignupStage4 = new InvitationCodeSignupStage();
    dummySignupStage4.setData(invitation2);

    new EqualsTester()
        .addEqualityGroup(dummySignupStage1, dummySignupStage2)
        .addEqualityGroup(dummySignupStage3, dummySignupStage4)
        .testEquals();

    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage1.hashCode());
    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage2.hashCode());
    assertThat(dummySignupStage1.hashCode()).isNotEqualTo(dummySignupStage3.hashCode());
  }

}
