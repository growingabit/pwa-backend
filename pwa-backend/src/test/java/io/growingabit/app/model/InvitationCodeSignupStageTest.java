package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class InvitationCodeSignupStageTest extends BaseDatastoreTest {

  private InvitationCodeSignupStageDao invitationCodeSignupStageDao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(InvitationCodeSignupStage.class);
    ObjectifyService.register(User.class);
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();
    this.userDao = new UserDao();
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

    final InvitationCodeSignupStage dummySignupStage1 = new InvitationCodeSignupStage();
    dummySignupStage1.setId(1L);
    final InvitationCodeSignupStage dummySignupStage2 = new InvitationCodeSignupStage();
    dummySignupStage2.setId(1L);

    final InvitationCodeSignupStage dummySignupStage3 = new InvitationCodeSignupStage();
    dummySignupStage3.setId(2L);

    final InvitationCodeSignupStage dummySignupStage4 = new InvitationCodeSignupStage();
    dummySignupStage4.setId(2L);

    new EqualsTester()
        .addEqualityGroup(dummySignupStage1, dummySignupStage2)
        .addEqualityGroup(dummySignupStage3, dummySignupStage4)
        .testEquals();

    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage1.hashCode());
    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage2.hashCode());
    assertThat(dummySignupStage1.hashCode()).isNotEqualTo(dummySignupStage3.hashCode());
  }

}
