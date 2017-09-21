package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.testUtils.BaseGaeTest;

public class ParentPhoneSignupStageTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";
  private static final String PARENT_FIRSTNAME = "firstname";
  private static final String PARENT_LASTNAME = "lastname";

  private ParentPhoneSignupStageDao ParentPhoneSignupStageDao;

  @Before
  public void setUp() {
    ObjectifyService.register(ParentPhoneSignupStage.class);
    this.ParentPhoneSignupStageDao = new ParentPhoneSignupStageDao();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
    final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME);

    stage.setData(data);
    stage.setData(null);
    assertThat(stage.getData()).isNotNull();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.ParentPhoneSignupStageDao.persist(new ParentPhoneSignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new ParentPhoneSignupStage().isDone()).isFalse();
  }

}
