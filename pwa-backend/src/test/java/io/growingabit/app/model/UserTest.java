package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.UserDao;
import io.growingabit.common.dao.BaseDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.DummySignupStage;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class UserTest extends BaseDatastoreTest {

  private BaseDao<DummySignupStage> baseDao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(User.class);
    this.baseDao = new BaseDao<>(DummySignupStage.class);
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

    final DummySignupStage addedStage = new DummySignupStage();
    addedStage.setUser(userKey);
    this.baseDao.persist(addedStage);

    final User user = new User();
    user.addSignupStage(addedStage);

    user.getSignupStages().add(new DummySignupStage());
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
      completedStage.setDone(true);

      this.baseDao.persist(completedStage);
      user.addSignupStage(completedStage);
    }

    assertThat(user.isSignupDone()).isTrue();
  }

  @Test
  public void signupIsNotDone() {
    final User model = new User();
    model.setId("id");
    this.userDao.persist(model);
    final Key<User> userKey = Key.create(model);

    final int n = new Random().nextInt(10) + 1;

    final User user = new User();
    DummySignupStage completedStage;
    for (int i = 1; i < n; i++) {
      completedStage = new DummySignupStage();
      completedStage.setDone(true);
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
  public void equalsAndHashCode() {

    final int n = new Random().nextInt(10) + 1;

    final User user1 = new User();
    final User user2 = new User();
    final User user3 = new User();
    final User user4 = new User();

    user1.setId("id1");
    user2.setId("id1");
    user3.setId("id2");
    user4.setId("id2");
    boolean b;
    DummySignupStage completedStage;
    for (int i = 1; i < n; i++) {
      b = Math.random() > 0.5;
      completedStage = new DummySignupStage();
      completedStage.setDone(b);
      completedStage.setUser(Key.create(user1));
      this.baseDao.persist(completedStage);
      user1.addSignupStage(completedStage);

      completedStage = new DummySignupStage();
      completedStage.setUser(Key.create(user2));
      completedStage.setDone(b);
      this.baseDao.persist(completedStage);
      user2.addSignupStage(completedStage);

      b = !b;
      completedStage = new DummySignupStage();
      completedStage.setDone(b);
      completedStage.setUser(Key.create(user3));
      this.baseDao.persist(completedStage);
      user3.addSignupStage(completedStage);

      completedStage = new DummySignupStage();
      completedStage.setUser(Key.create(user4));
      completedStage.setDone(b);
      this.baseDao.persist(completedStage);
      user4.addSignupStage(completedStage);
    }

    new EqualsTester()
        .addEqualityGroup(user1, user2)
        .addEqualityGroup(user3, user4)
        .testEquals();

    assertThat(user1.hashCode()).isEqualTo(user1.hashCode());
    assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
  }

}
