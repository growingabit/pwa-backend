package io.growingabit.app.dao;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.model.User;
import io.growingabit.testUtils.BaseGaeTest;

public class UserDaoTest extends BaseGaeTest {

  private UserDao userDao;

  @Before
  public void setup() {
    ObjectifyService.factory().register(User.class);
    this.userDao = new UserDao();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExistNullKey() {
    final Key<User> userKey = null;
    this.userDao.exist(userKey);
  }

  @Test
  public void testExist() {
    final User user = new User();
    user.setId("id");
    this.userDao.persist(user);
    final boolean exist = this.userDao.exist(Key.create(user));
    assertThat(exist).isTrue();
  }

  @Test
  public void testNotExist() {
    final User user = new User();
    user.setId("id");
    this.userDao.persist(user);
    this.userDao.delete(user);
    final boolean exist = this.userDao.exist(Key.create(user));
    assertThat(exist).isFalse();
  }
}
