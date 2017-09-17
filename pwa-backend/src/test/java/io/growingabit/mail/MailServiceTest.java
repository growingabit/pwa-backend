package io.growingabit.mail;

import static com.google.common.truth.Truth.assertThat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.testUtils.BaseGaeTest;

public class MailServiceTest extends BaseGaeTest {

  private static StudentEmailSignupStage stage = new StudentEmailSignupStage();

  @BeforeClass
  public static void setUp() {
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
    stage.setData(data);
  }

  @Test
  public void isVerificationCodeNotEmpty() {
    try {
      Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      String verificationCode = (String) method.invoke(null, stage);

      assertThat(verificationCode).isNotEmpty();
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Assert.fail();
    }
  }

  @Test
  public void isVerificationCodeUsesTheRigthEndpoint() {
    try {
      Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      String verificationCode = (String) method.invoke(null, stage);

      assertThat(verificationCode).contains("/verificationemail/");
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Assert.fail();
    }
  }

  @Test
  public void isVerificationCodeHasCodeEncoded() {
    try {
      Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      String verificationCode = (String) method.invoke(null, stage);

      String code = new String(Base64.decodeBase64(verificationCode.replace("/verificationemail/", "")), "utf-8");
      assertThat(stage.getData().getVerificationCode()).isEqualTo(code);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | UnsupportedEncodingException e) {
      Assert.fail();
    }
  }

  @Test
  public void isVerificationLinkIsValid() {
    try {
      Method method = MailService.class.getDeclaredMethod("createVerificationLink", StudentEmailSignupStage.class);
      method.setAccessible(true);
      String verificationLink = (String) method.invoke(null, stage);
      assertThat(verificationLink).contains("http");
      assertThat(verificationLink.contains("localhost") || verificationLink.contains("appspot.com")).isTrue();
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Assert.fail();
    }
  }

  @Test
  public void checkRecipient() {
    try {
      Message message = MailService.sendVerificationEmail(stage);
      List<Address> to = Arrays.asList(message.getRecipients(RecipientType.TO));
      Boolean found = false;
      for (Address address : to) {
        if (address.toString().contains(stage.getData().getEmail())) {
          found = true;
        }
      }

      assertThat(found).isTrue();

    } catch (UnsupportedEncodingException | MessagingException e) {
      Assert.fail();
    }
  }

}
