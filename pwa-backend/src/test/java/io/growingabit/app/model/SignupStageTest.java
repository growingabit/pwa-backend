package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.UserDao;
import io.growingabit.common.dao.BaseDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.DummySignupStage;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class SignupStageTest extends BaseDatastoreTest {

  private BaseDao<DummySignupStage> baseDao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(io.growingabit.testUtils.DummySignupStage.class);
    ObjectifyService.register(User.class);
    this.baseDao = new BaseDao<>(io.growingabit.testUtils.DummySignupStage.class);
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
  public void equalsAndHashCode() {

    final int n = new Random().nextInt(10) + 1;

    final DummySignupStage dummySignupStage1 = new DummySignupStage();
    dummySignupStage1.setId(1L);
    final DummySignupStage dummySignupStage2 = new DummySignupStage();
    dummySignupStage2.setId(1L);

    final DummySignupStage dummySignupStage3 = new DummySignupStage();
    dummySignupStage3.setId(2L);

    final DummySignupStage dummySignupStage4 = new DummySignupStage();
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
