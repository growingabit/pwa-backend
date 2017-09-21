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

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.testUtils.BaseGaeTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Twilio.class, Message.class})
public class DeferredTaskSendParentVerificationSMSTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";
  private static final String PARENT_FIRSTNAME = "firstname";
  private static final String PARENT_LASTNAME = "lastname";

  @Before
  public void setUp() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(ParentPhoneSignupStage.class);
  }

  @Test
  public void keyNull() {
    final DeferredTaskSendParentVerificationSMS d = new DeferredTaskSendParentVerificationSMS(null, null);
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void keyEmpty() {
    final DeferredTaskSendParentVerificationSMS d = new DeferredTaskSendParentVerificationSMS("", "");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void notAKey() {
    final DeferredTaskSendParentVerificationSMS d = new DeferredTaskSendParentVerificationSMS("foo", "foo");
    try {
      d.run();
      assert true;
    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

  @Test
  public void wrongKey() {
    final DeferredTaskSendParentVerificationSMS d = new DeferredTaskSendParentVerificationSMS(Key.create(ParentPhoneSignupStage.class, 1L).toWebSafeString(), Key.create(StudentDataSignupStage.class, 1L).toWebSafeString());
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

      final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
      stage.setData(new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME));
      stage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final StudentDataSignupStage dataStage = new StudentDataSignupStage();
      final StudentData studentData = new StudentData();
      studentData.setBirthdate("01/01/1900");
      studentData.setName("name");
      studentData.setSurname("surname");
      dataStage.setData(studentData);
      dataStage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendParentVerificationSMS task = new DeferredTaskSendParentVerificationSMS(stage.getWebSafeKey(), dataStage.getWebSafeKey());
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

      final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
      stage.setData(new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME));
      stage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final StudentDataSignupStage dataStage = new StudentDataSignupStage();
      final StudentData studentData = new StudentData();
      studentData.setBirthdate("01/01/1900");
      studentData.setName("name");
      studentData.setSurname("surname");
      dataStage.setData(studentData);
      dataStage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendParentVerificationSMS task = new DeferredTaskSendParentVerificationSMS(stage.getWebSafeKey(), dataStage.getWebSafeKey());
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

      final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
      stage.setData(new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME));
      stage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final StudentDataSignupStage dataStage = new StudentDataSignupStage();
      final StudentData studentData = new StudentData();
      studentData.setBirthdate("01/01/1900");
      studentData.setName("name");
      studentData.setSurname("surname");
      dataStage.setData(studentData);
      dataStage.setUser(Key.create(User.class, user.getId()));
      new ParentPhoneSignupStageDao().persist(stage);

      final DeferredTaskSendParentVerificationSMS task = new DeferredTaskSendParentVerificationSMS(stage.getWebSafeKey(), dataStage.getWebSafeKey());
      task.run();

      assert true;

    } catch (final Throwable t) {
      Assert.fail("Should not throws exception " + ExceptionUtils.getStackTrace(t));
    }
  }

}
