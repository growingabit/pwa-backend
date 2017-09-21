package io.growingabit.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.text.StrSubstitutor;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.common.collect.ImmutableMap;

import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.GoogleUrlShortenerService;
import io.growingabit.app.utils.Settings;

import com.google.common.collect.ImmutableMap;

import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.GoogleUrlShortenerService;
import io.growingabit.app.utils.Settings;

public class MailService {

  private static final XLogger log = XLoggerFactory.getXLogger(MailService.class);

  public static Message sendVerificationEmail(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException, MessagingException {

    final String verificationLink = createVerificationLink(studentEmailSignupStage);
    final String shortenLink = new GoogleUrlShortenerService().insertSafe(verificationLink);

    log.info(shortenLink);

    final String subject = Settings.getConfig().getString("io.growingabit.mail.verifyemail.subject");
    final String htmlBody = new StrSubstitutor(ImmutableMap.of("verificationLink", shortenLink)).replace(Settings.getConfig().getString("io.growingabit.mail.verifyemail.template"));

    final Message message = new EmailMessageBuilder(studentEmailSignupStage.getData().getEmail(), subject).addBcc(Settings.getConfig().getString("io.growingabit.mail.bcc")).withHtmlBody(htmlBody).build();
    Transport.send(message);
    return message;
  }

  private static String createVerificationLink(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException {
    final String verificationLink = "https://" + studentEmailSignupStage.getData().getOriginHost();
    return verificationLink + createVerificationCode(studentEmailSignupStage);
  }

  private static String createVerificationCode(final StudentEmailSignupStage studentEmailSignupStage) throws UnsupportedEncodingException {
    return "/verify/email/" + Base64.encodeBase64URLSafeString(studentEmailSignupStage.getData().getVerificationCode().getBytes("utf-8"));
  }

}
