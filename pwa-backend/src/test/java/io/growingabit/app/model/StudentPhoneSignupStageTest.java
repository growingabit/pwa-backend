package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.testUtils.BaseGaeTest;

public class StudentPhoneSignupStageTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";

  private StudentPhoneSignupStageDao studentPhoneSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(StudentPhoneSignupStage.class);
    this.studentPhoneSignupStageDao = new StudentPhoneSignupStageDao();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
    final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006", HOST);

    stage.setData(data);
    stage.setData(null);
    assertThat(stage.getData()).isNotNull();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.studentPhoneSignupStageDao.persist(new StudentPhoneSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new StudentPhoneSignupStage().isDone()).isFalse();
  }

}
