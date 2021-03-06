package io.growingabit.app.model.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.User;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.testUtils.BaseGaeTest;

public class ReferenceSignupStageTest extends BaseGaeTest {

  private UserDao userDao;

  @Before
  public void setup() {
    ObjectifyService.factory().register(User.class);
    this.userDao = new UserDao();
  }

  @Test
  public void getShouldReturnNull() {
    final DummyReferenceSignupStage ref = new DummyReferenceSignupStage();
    final User s = ref.getData();
    assertThat(s).isNull();
  }

  @Test
  public void setShouldNotAcceptNull() {
    final DummyReferenceSignupStage ref = new DummyReferenceSignupStage();
    final User u = new User();
    u.setId("id");
    this.userDao.persist(u);
    ref.setData(u);
    ref.setData(null);
    final User s = ref.getData();
    assertThat(s).isNotNull();
  }

  private class DummyReferenceSignupStage extends ReferenceSignupStage<User> {

    @Override
    public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {

    }
  }

}
