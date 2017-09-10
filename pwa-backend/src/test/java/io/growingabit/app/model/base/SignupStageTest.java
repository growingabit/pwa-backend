package io.growingabit.app.model.base;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.User;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.common.dao.BaseDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.DummySignupStage;
import org.junit.Before;
import org.junit.Test;

public class SignupStageTest extends BaseDatastoreTest {

  private BaseDao<DummySignupStage> baseDao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(User.class);
    this.baseDao = new BaseDao<>(DummySignupStage.class);
    this.userDao = new UserDao();
  }

  @Test(expected = SaveException.class)
  public void userIsRequired() {
    this.baseDao.persist(new DummySignupStage());
  }

  @Test
  public void isNotDoneByDefault() {
    assertThat(new DummySignupStage().isDone()).isFalse();
  }

  @Test
  public void shouldNotAcceptNullUser() {
    final DummySignupStage ref = new DummySignupStage();
    final User u = new User();
    u.setId("id");
    this.userDao.persist(u);
    ref.setUser(Key.create(u));
    ref.setData(null);
    final Key<User> s = ref.getUser();
    assertThat(s).isNotNull();
  }

  @Test
  public void equalsAndHashCode() {

    final DummySignupStage dummySignupStage1 = new DummySignupStage();
    dummySignupStage1.setDone();
    final DummySignupStage dummySignupStage2 = new DummySignupStage();
    dummySignupStage2.setDone();

    final DummySignupStage dummySignupStage3 = new DummySignupStage();
    final DummySignupStage dummySignupStage4 = new DummySignupStage();

    new EqualsTester()
        .addEqualityGroup(dummySignupStage1, dummySignupStage2)
        .addEqualityGroup(dummySignupStage3, dummySignupStage4)
        .testEquals();

    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage1.hashCode());
    assertThat(dummySignupStage1.hashCode()).isEqualTo(dummySignupStage2.hashCode());
    assertThat(dummySignupStage1.hashCode()).isNotEqualTo(dummySignupStage3.hashCode());
  }

  @Test(expected = IllegalStateException.class)
  public void throwIllegalStateIfIdentifierIsMissing() {
    new DummyMissingIDentifierSignupStage();
  }

  private class DummyMissingIDentifierSignupStage extends SignupStage {

    @Override
    public Object getData() {
      return null;
    }

    @Override
    public void setData(final Object data) {

    }

    @Override
    public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {

    }
  }

}
