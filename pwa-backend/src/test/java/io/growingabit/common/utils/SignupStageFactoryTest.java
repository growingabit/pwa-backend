package io.growingabit.common.utils;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.DummySignupStage;
import io.growingabit.testUtils.Utils;

public class SignupStageFactoryTest extends BaseGaeTest {

  private UserDao userDao;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.registerMandatory(DummySignupStage.class);
    this.userDao = new UserDao();
  }

  @Test
  public void shouldReturnRegisteredSignupStages() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getSignupStages(Key.create(u));
      assertThat(list).hasSize(1);
      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }


  @Test
  public void userKeyShouldBeSetted() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getSignupStages(Key.create(u));
      assertThat(list.get(0).getUser()).isEqualTo(Key.create(u));
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void signupStageIndentifierShouldBeSetted() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getSignupStages(Key.create(u));

      final String stageIdentifier = Settings.getConfig().getString(DummySignupStage.class.getCanonicalName());
      assertThat(list.get(0).getStageIdentifier()).isEqualTo(stageIdentifier);
      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void signupStageIndentifierShouldBeInstancesOfRegisteredOnes() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getSignupStages(Key.create(u));

      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void shouldRegisterOnlyOneInstanceOfEverySignupStage() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);

      final int n = new Random().nextInt(100);
      for (int i = 0; i < n; i++) {
        SignupStageFactory.register(DummySignupStage.class);
      }
      assertThat(SignupStageFactory.getSignupStages(Key.create(u)).size()).isEqualTo(1);
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void shouldReturnRegisteredMandatorySignupStages() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getMandatorySignupStages(Key.create(u));
      assertThat(list).hasSize(1);
      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }


  @Test
  public void userKeyShouldBeSettedInMandatorySignupStage() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getMandatorySignupStages(Key.create(u));
      assertThat(list.get(0).getUser()).isEqualTo(Key.create(u));
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void mandatorySignupStageIndentifierShouldBeSetted() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getMandatorySignupStages(Key.create(u));

      final String stageIdentifier = Settings.getConfig().getString(DummySignupStage.class.getCanonicalName());
      assertThat(list.get(0).getStageIdentifier()).isEqualTo(stageIdentifier);
      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void mandatorySignupStageIndentifierShouldBeInstancesOfRegisteredOnes() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);
      final List<SignupStage> list = SignupStageFactory.getMandatorySignupStages(Key.create(u));

      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }

  @Test
  public void shouldRegisterOnlyOneInstanceOfEveryMandatorySignupStage() {
    try {
      final User u = new User();
      u.setId("id");
      this.userDao.persist(u);

      final int n = new Random().nextInt(100);
      for (int i = 0; i < n; i++) {
        SignupStageFactory.register(DummySignupStage.class);
      }
      assertThat(SignupStageFactory.getMandatorySignupStages(Key.create(u)).size()).isEqualTo(1);
    } catch (final Exception e) {
      assert false;
    }
  }

}
