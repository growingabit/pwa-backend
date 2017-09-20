package io.growingabit.app.tasks.deferred;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.testUtils.BaseGaeTest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeferredTaskSendVerificationEmailTest extends BaseGaeTest {

  @Before
  public void setUp() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);
  }

  @Test
  public void keyNull() {
    final DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail(null);
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void keyEmpty() {
    final DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail("");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void notAKey() {
    final DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail("foo");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void wrongKey() {
    final DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail(Key.create(StudentEmailSignupStage.class, 1L).toWebSafeString());
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void correctParams() {

    try {
      final User user = new User();
      user.setId("id");
      new UserDao().persist(user);

      final StudentEmailSignupStage stage = new StudentEmailSignupStage();
      stage.setData(new StudentConfirmationEmail("test@example.com"));
      stage.setUser(Key.create(User.class, user.getId()));
      new StudentEmailSignupStageDao().persist(stage);

      final DeferredTaskSendVerificationEmail task = new DeferredTaskSendVerificationEmail(stage.getWebSafeKey());
      task.run();

      assert true;

    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }

  }

}
