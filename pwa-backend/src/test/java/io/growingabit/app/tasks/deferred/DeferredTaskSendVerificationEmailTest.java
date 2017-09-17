package io.growingabit.app.tasks.deferred;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.gson.GsonFactory;
import io.growingabit.testUtils.BaseGaeTest;

public class DeferredTaskSendVerificationEmailTest extends BaseGaeTest {

  @Before
  public void setUp() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);
  }

  @Test
  public void keyNull() {
    DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail(null);
    try {
      d.run();
      assert true;
    } catch (Throwable t) {
      Assert.fail("Should not throws exception");
    }
  }

  @Test
  public void keyEmpty() {
    DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail("");
    try {
      d.run();
      assert true;
    } catch (Throwable t) {
      Assert.fail("Should not throws exception");
    }
  }

  @Test
  public void notAKey() {
    DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail("foo");
    try {
      d.run();
      assert true;
    } catch (Throwable t) {
      Assert.fail("Should not throws exception");
    }

  }

  @Test
  public void wrongKey() {
    DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail(Key.create(StudentEmailSignupStage.class, 1L).toWebSafeString());
    try {
      d.run();
      assert true;
    } catch (Throwable t) {
      Assert.fail("Should not throws exception");
    }
  }


  @Test
  @Ignore
  public void checkEquals() {

    try {
      StudentEmailSignupStage s = new StudentEmailSignupStage();
      s.setData(new StudentConfirmationEmail("test@example.com"));

      User user = new User();
      user.setId("id");
      new UserDao().persist(user);
      s.setUser(Key.create(User.class, user.getId()));

      StudentEmailSignupStageDao dao = new StudentEmailSignupStageDao();
      dao.persist(s);

      DeferredTaskSendVerificationEmail d = new DeferredTaskSendVerificationEmail(s.getWebSafeKey());

      Field f = DeferredTaskSendVerificationEmail.class.getDeclaredField("studentEmailSignupStageWebsafeString");
      f.setAccessible(true);
      String studentEmailSignupStageWebsafeString = (String) f.get(d);
      StudentEmailSignupStage stage = dao.find(studentEmailSignupStageWebsafeString);

      Assert.assertNotNull(stage.getData().getEmail());
      Assert.assertNotNull(stage.getData().getVerificationCode());
      Assert.assertNotNull(stage.getData().getTsExpiration());

      Assert.assertEquals(GsonFactory.getGsonInstance().toJson(s), GsonFactory.getGsonInstance().toJson(stage));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }


}
