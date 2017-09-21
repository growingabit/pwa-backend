package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.StudentDataSignupStageDao;
import io.growingabit.testUtils.BaseGaeTest;

public class StudentDataSignupStageTest extends BaseGaeTest {

  private StudentDataSignupStageDao studentDataSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(StudentDataSignupStage.class);
    this.studentDataSignupStageDao = new StudentDataSignupStageDao();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final StudentDataSignupStage stage = new StudentDataSignupStage();
    final StudentData data = new StudentData();
    data.setName("Lorenzo");
    data.setSurname("Bugiani");
    data.setBirthdate("19/04/1985");

    stage.setData(data);
    stage.setData(null);
    assertThat(stage.getData()).isNotNull();
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
