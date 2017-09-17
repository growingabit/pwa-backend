package io.growingabit.mail;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.ImmutableMap;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.Settings;
import java.io.UnsupportedEncodingException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.text.StrSubstitutor;

public class MailService {

  public static Message sendVerificationEmail(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException, MessagingException {

    final String verificationLink = createVerificationLink(studentEmailSignupStage);

    final String subject = "[Growbit] Verifiy your email";
    final String htmlBody = new StrSubstitutor(ImmutableMap.of("verificationLink", verificationLink)).replace(Settings.getConfig().getString("io.growingabit.mail.verifyemail.template"));

    final Message message = new EmailMessageBuilder(studentEmailSignupStage.getData().getEmail(), subject).addBcc(Settings.getConfig().getString("io.growingabit.mail.bcc")).withHtmlBody(htmlBody).build();
    Transport.send(message);
    return message;
  }

  private static String createVerificationLink(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException {
    String verificationLink = "";
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      verificationLink += "https://" + SystemProperty.applicationId.get() + ".appspot.com";
    } else {
      verificationLink += "http://localhost:8080";
    }
    return verificationLink + createVerificationCode(studentEmailSignupStage);
  }

  private static String createVerificationCode(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException {
    return "/verificationemail/" + Base64.encodeBase64URLSafeString(studentEmailSignupStage.getData().getVerificationCode().getBytes("utf-8"));
  }

}
