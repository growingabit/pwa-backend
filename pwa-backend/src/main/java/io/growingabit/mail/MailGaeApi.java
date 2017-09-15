package io.growingabit.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import io.growingabit.app.utils.Settings;

public class MailGaeApi {

  private static final XLogger log = XLoggerFactory.getXLogger(MailGaeApiTest.class);

  public static Message sendEmail(final MailObject mailObject) throws UnsupportedEncodingException, MessagingException {

    log.entry(mailObject);

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.sender"), "The Growbit Team"));

    for (String to : mailObject.getTo()) {
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    }
    for (String cc : mailObject.getCc()) {
      msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
    }
    for (String bcc : mailObject.getBcc()) {
      msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
    }

    msg.setReplyTo(new Address[] {new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.replyTo"))});

    msg.setSubject(mailObject.getSubject());
    switch (mailObject.getBodyType()) {
      default:
      case TEXT:
        msg.setText(mailObject.getBody());
        break;
      case HTML:
        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(mailObject.getBody(), MediaType.TEXT_HTML);
        mp.addBodyPart(htmlPart);
        msg.setContent(mp);
        break;
    }

    Transport.send(msg);
    log.info("Mail inviata");

    return msg;
  }

}
