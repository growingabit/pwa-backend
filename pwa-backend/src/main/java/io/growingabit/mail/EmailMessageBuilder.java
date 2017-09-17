package io.growingabit.mail;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.Settings;

public class EmailMessageBuilder {

  private String subject;
  private String body;
  private BodyType bodyType;
  private Set<String> to;
  private Set<String> cc;
  private Set<String> bcc;

  public EmailMessageBuilder(String to, String subject) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(to));

    this.to = new LinkedHashSet<String>();
    this.cc = new LinkedHashSet<String>();
    this.bcc = new LinkedHashSet<String>();

    this.to.add(to);
    this.subject = subject;
  }

  public EmailMessageBuilder withHtmlBody(String htmlBody) {
    this.body = htmlBody;
    this.bodyType = BodyType.HTML;
    return this;
  }

  public EmailMessageBuilder withTextBody(String textBody) {
    this.body = textBody;
    this.bodyType = BodyType.TEXT;
    return this;
  }

  public EmailMessageBuilder addTo(String address) {
    return this.addRecipient(address, this.to);
  }

  public EmailMessageBuilder addTo(Collection<String> address) {
    return this.addRecipients(address, this.to);
  }

  public EmailMessageBuilder addCc(String address) {
    return this.addRecipient(address, this.cc);
  }

  public EmailMessageBuilder addCc(Collection<String> address) {
    return this.addRecipients(address, this.cc);
  }

  public EmailMessageBuilder addBcc(String address) {
    return this.addRecipient(address, this.bcc);
  }

  public EmailMessageBuilder addBcc(Collection<String> address) {
    return this.addRecipients(address, this.bcc);
  }

  private EmailMessageBuilder addRecipient(String address, Set<String> recipientsList) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
    recipientsList.add(address);
    return this;
  }

  private EmailMessageBuilder addRecipients(Collection<String> addressList, Set<String> recipientsList) {
    for (String a : addressList) {
      this.addRecipient(a, recipientsList);
    }
    return this;
  }

  public Message build() throws UnsupportedEncodingException, MessagingException {
    Preconditions.checkArgument(this.body != null && !this.body.isEmpty());

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.sender"), "The Growbit Team"));

    for (String to : this.to) {
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    }
    for (String cc : this.cc) {
      msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
    }
    for (String bcc : this.bcc) {
      msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
    }

    msg.setReplyTo(new Address[] {new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.replyTo"))});

    msg.setSubject(this.subject);
    switch (this.bodyType) {
      default:
      case TEXT:
        msg.setText(this.body);
        break;
      case HTML:
        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(this.body, MediaType.TEXT_HTML);
        mp.addBodyPart(htmlPart);
        msg.setContent(mp);
        break;
    }
    return msg;
  }

  public enum BodyType {
    TEXT, HTML
  }

}

