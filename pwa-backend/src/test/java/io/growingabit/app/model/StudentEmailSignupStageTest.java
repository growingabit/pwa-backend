package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import javax.mail.internet.AddressException;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.testUtils.BaseDatastoreTest;

public class StudentEmailSignupStageTest extends BaseDatastoreTest {

  private StudentEmailSignupStageDao studentEmailSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(StudentEmailSignupStage.class);
    this.studentEmailSignupStageDao = new StudentEmailSignupStageDao();
  }

  @Test
  public void setShouldNotAcceptNull() throws AddressException {
    final StudentEmailSignupStage stage = new StudentEmailSignupStage();
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");

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
