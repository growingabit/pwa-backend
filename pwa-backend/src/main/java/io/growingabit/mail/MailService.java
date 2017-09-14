package io.growingabit.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.utils.SystemProperty;

import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.Settings;

public class MailService {

  public static Message sendVerificationEmail(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException, MessagingException {

    String verificationLink = "";
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      verificationLink += "https://" + SystemProperty.applicationId.get() + ".appspot.com";
    } else {
      verificationLink += "http://locahost:8888";
    }
    verificationLink += "/verificationemail/" + Base64.encodeBase64URLSafeString(studentEmailSignupStage.getData().getVerificationCode().getBytes("utf-8"));

    String subject = "[Growbit] Verifiy your email";
    String htmlBody = "<p>Welcome to Growbit!</p>";
    htmlBody += "<p>Please verify your email address by clicking this link:</p>";
    htmlBody += "<p><a href='" + verificationLink + "'></p>";
    htmlBody += "<p>This link will expire in seven days</p>";
    htmlBody += "<p></p>";
    htmlBody += "<p><i>Growbit team</i></p>";

    MailObject mailObject = new MailObject.Builder(studentEmailSignupStage.getData().getEmail(), subject).addBcc(Settings.getConfig().getString("io.growingabit.mail.bcc")).withHtmlBody(htmlBody).build();

    return MailGaeApi.sendEmail(mailObject);
  }

}
