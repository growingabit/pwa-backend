package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.common.dao.BaseDao;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.DummySignupStage;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class UserTest extends BaseGaeTest {

  private BaseDao<SignupStage> baseDao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(User.class);
    this.baseDao = new BaseDao<>(SignupStage.class);
    this.userDao = new UserDao();
  }

  @Test
  public void creationDateTest() {
    final User model = new User();
    assertThat(model.getCreationDate()).isEqualTo(-1L);
    model.setId("id");
    this.userDao.persist(model);
    assertThat(model.getCreationDate()).isGreaterThan(0L);
  }

  @Test
  public void modifiedDateTest() throws InterruptedException {
    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    assertThat(model.getCreationDate()).isEqualTo(model.getModifiedDate());
    Thread.sleep(200);
    this.userDao.persist(model);
    assertThat(model.getModifiedDate()).isGreaterThan(model.getCreationDate());
  }

  @Test
  public void webSafeKeyTest() throws InterruptedException {
    final User model = new User();
    assertThat(model.getWebSafeKey()).isNull();
    model.setId("id");
    this.userDao.persist(model);
    assertThat(model.getWebSafeKey()).isNotNull();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void signupStageListShouldBeImmutable() {

    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final SignupStage addedStage = new DummySignupStage();
    addedStage.setUser(userKey);
    this.baseDao.persist(addedStage);

    final User user = new User();
    user.addSignupStage(addedStage);
    user.getSignupStages().put("1", Ref.create(addedStage));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void mandatotySignupStageListShouldBeImmutable() {

    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final SignupStage addedStage = new DummySignupStage();
    addedStage.setUser(userKey);
    this.baseDao.persist(addedStage);

    final User user = new User();
    user.addMandatorySignupStage(addedStage);
    user.getMandatorySignupStages().put("1", Ref.create(addedStage));
  }

  @Test
  public void signupIsDone() {
    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final int n = new Random().nextInt(10) + 1;

    final User user = new User();
    DummySignupStage completedStage;

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setUser(userKey);
      completedStage.setDone();

      this.baseDao.persist(completedStage);
      user.addMandatorySignupStage(completedStage);
    }

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setUser(userKey);
      completedStage.setDone();

      this.baseDao.persist(completedStage);
      user.addSignupStage(completedStage);
    }

    assertThat(user.isSignupDone()).isTrue();
  }

  @Test
  public void signupIsNotDoneIfAtLeatOneMandatorySignupStageIsNotDone() {
    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final int n = new Random().nextInt(10) + 1;

    final User user = new User();
    DummySignupStage completedStage;

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setDone();
      completedStage.setUser(userKey);
      this.baseDao.persist(completedStage);
      user.addMandatorySignupStage(completedStage);
    }

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setDone();
      completedStage.setUser(userKey);
      this.baseDao.persist(completedStage);
      user.addSignupStage(completedStage);
    }

    final DummySignupStage uncompletedStage = new DummySignupStage();
    uncompletedStage.setUser(userKey);
    this.baseDao.persist(uncompletedStage);
    user.addMandatorySignupStage(uncompletedStage);

    assertThat(user.isSignupDone()).isFalse();
  }

  @Test
  public void signupIsNotDoneIfAtLeatOneSignupStageIsNotDone() {
    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final int n = new Random().nextInt(10) + 1;

    final User user = new User();
    DummySignupStage completedStage;

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setDone();
      completedStage.setUser(userKey);
      this.baseDao.persist(completedStage);
      user.addMandatorySignupStage(completedStage);
    }

    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setDone();
      completedStage.setUser(userKey);
      this.baseDao.persist(completedStage);
      user.addSignupStage(completedStage);
    }

    final DummySignupStage uncompletedStage = new DummySignupStage();
    uncompletedStage.setUser(userKey);
    this.baseDao.persist(uncompletedStage);
    user.addSignupStage(uncompletedStage);

    assertThat(user.isSignupDone()).isFalse();
  }

  @Test
  public void shouldNotAcceptNullId() {
    final User model = new User();
    model.setId("id");
    model.setId(null);
    assertThat(model.getId()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptyId() {
    final User model = new User();
    model.setId("id");
    model.setId("");
    assertThat(model.getId()).isNotEmpty();
  }

}
