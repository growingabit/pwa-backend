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

  private static final String ORIGIN = "http://localhost";
  private static final String HOST = "localhost";
  private static final String EMAIL_EXAMPLE_COM = "email@example.com";
  private static final StudentEmailSignupStage stage = new StudentEmailSignupStage();

  @BeforeClass
  public static void setUp() {
    final StudentConfirmationEmail data = new StudentConfirmationEmail(EMAIL_EXAMPLE_COM, ORIGIN);
    stage.setData(data);
  }

  @Test
  public void verificationCodeNotEmpty() {
    try {
      final Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      final String verificationCode = (String) method.invoke(null, stage);

      assertThat(verificationCode).isNotEmpty();
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Assert.fail();
    }
  }

  @Test
  public void verificationCodeUsesTheRigthEndpoint() {
    try {
      final Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      final String verificationCode = (String) method.invoke(null, stage);

      assertThat(verificationCode).contains("/verify/email/");
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      Assert.fail();
    }
  }

  @Test
  public void verificationCodeHasCodeEncoded() {
    try {
      final Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      final String verificationCode = (String) method.invoke(null, stage);

      final String code = new String(Base64.decodeBase64(verificationCode.replace("/verify/email/", "")), "utf-8");
      assertThat(stage.getData().getVerificationCode()).isEqualTo(code);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | UnsupportedEncodingException e) {
      Assert.fail();
    }
  }

  @Test
  public void checkRecipient() {
    try {
      final Message message = MailService.sendVerificationEmail(stage);
      final List<Address> to = Arrays.asList(message.getRecipients(RecipientType.TO));
      Boolean found = false;
      for (final Address address : to) {
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
