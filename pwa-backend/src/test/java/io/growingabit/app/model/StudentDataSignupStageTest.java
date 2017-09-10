package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.StudentDataSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import org.junit.Before;
import org.junit.Test;

public class StudentDataSignupStageTest extends BaseDatastoreTest {

  private UserDao userDao;
  private InvitationDao invitationDao;
  private StudentDataSignupStageDao studentDataSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(StudentDataSignupStage.class);
    ObjectifyService.register(User.class);
    this.studentDataSignupStageDao = new StudentDataSignupStageDao();
    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.studentDataSignupStageDao.persist(new StudentDataSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new StudentDataSignupStage().isDone()).isFalse();
  }

}
