package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.testUtils.BaseGaeTest;

public class StudentEmailSignupStageTest extends BaseGaeTest {

  private StudentEmailSignupStageDao studentEmailSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(StudentEmailSignupStage.class);
    this.studentEmailSignupStageDao = new StudentEmailSignupStageDao();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final StudentEmailSignupStage stage = new StudentEmailSignupStage();
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", "http://localhost");

    stage.setData(data);
    stage.setData(null);
    assertThat(stage.getData()).isNotNull();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.studentEmailSignupStageDao.persist(new StudentEmailSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new StudentEmailSignupStage().isDone()).isFalse();
  }

}
