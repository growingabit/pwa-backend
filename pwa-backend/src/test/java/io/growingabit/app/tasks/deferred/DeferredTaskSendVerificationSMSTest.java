package io.growingabit.app.tasks.deferred;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.testUtils.BaseGaeTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Twilio.class, Message.class})
public class DeferredTaskSendVerificationSMSTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";

  @Before
  public void setUp() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentPhoneSignupStage.class);
  }

  @Test
  public void keyNull() {
    final DeferredTaskSendVerificationSMS d = new DeferredTaskSendVerificationSMS(null);
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void keyEmpty() {
    final DeferredTaskSendVerificationSMS d = new DeferredTaskSendVerificationSMS("");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void notAKey() {
    final DeferredTaskSendVerificationSMS d = new DeferredTaskSendVerificationSMS("foo");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void wrongKey() {
    final DeferredTaskSendVerificationSMS d = new DeferredTaskSendVerificationSMS(Key.create(StudentPhoneSignupStage.class, 1L).toWebSafeString());
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void twilioInitThrowException() {
    try {
      PowerMockito.mockStatic(Twilio.class);
      PowerMockito.doThrow(new RuntimeException()).when(Twilio.class, "init", Mockito.anyString(), Mockito.anyString());

      final User user = new User();
      user.setId("id");
      new UserDao().persist(user);

      final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
      stage.setData(new StudentConfirmationPhone("+15005550006", HOST));
      stage.setUser(Key.create(User.class, user.getId()));
      new StudentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendVerificationSMS task = new DeferredTaskSendVerificationSMS(stage.getWebSafeKey());
      task.run();

      assert true;

    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void messageCreatorException() {
    try {
      PowerMockito.mockStatic(Message.class);
      PowerMockito.doThrow(new RuntimeException()).when(Message.class, "creator", Mockito.any(PhoneNumber.class), Mockito.any(PhoneNumber.class), Mockito.anyString());

      final User user = new User();
      user.setId("id");
      new UserDao().persist(user);

      final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
      stage.setData(new StudentConfirmationPhone("+15005550006", HOST));
      stage.setUser(Key.create(User.class, user.getId()));
      new StudentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendVerificationSMS task = new DeferredTaskSendVerificationSMS(stage.getWebSafeKey());
      task.run();

      assert true;

    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void createMessageException() {
    try {

      final MessageCreator messageCreator = Mockito.mock(MessageCreator.class);
      Mockito.when(messageCreator.create()).thenThrow(new RuntimeException());

      PowerMockito.mockStatic(Message.class);
      PowerMockito.when(Message.creator(Mockito.any(PhoneNumber.class), Mockito.any(PhoneNumber.class), Mockito.anyString())).thenReturn(messageCreator);

      final User user = new User();
      user.setId("id");
      new UserDao().persist(user);

      final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
      stage.setData(new StudentConfirmationPhone("+15005550006", HOST));
      stage.setUser(Key.create(User.class, user.getId()));
      new StudentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendVerificationSMS task = new DeferredTaskSendVerificationSMS(stage.getWebSafeKey());
      task.run();

      assert true;

    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

}
