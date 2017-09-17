package io.growingabit.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class MailGaeApi {

  private static final XLogger log = XLoggerFactory.getXLogger(MailGaeApi.class);

  public static Message sendEmail(final Message message) throws UnsupportedEncodingException, MessagingException {
    log.entry(message);

    Transport.send(message);

    log.info("Mail inviata");
    log.exit(message);
    return message;
  }

}
