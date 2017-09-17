package io.growingabit.mail;

import com.google.common.base.Preconditions;
import io.growingabit.app.utils.Settings;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailMessageBuilder {

  private final String subject;
  private String body;
  private BodyType bodyType;
  private final Set<String> to;
  private final Set<String> cc;
  private final Set<String> bcc;

  public EmailMessageBuilder(final String to, final String subject) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(to));
    Preconditions.checkArgument(StringUtils.isNotEmpty(subject));

    this.to = new LinkedHashSet<>();
    this.cc = new LinkedHashSet<>();
    this.bcc = new LinkedHashSet<>();

    this.to.add(to);
    this.subject = subject;
  }

  public EmailMessageBuilder withHtmlBody(final String htmlBody) {
    this.body = htmlBody;
    this.bodyType = BodyType.HTML;
    return this;
  }

  public EmailMessageBuilder withTextBody(final String textBody) {
    this.body = textBody;
    this.bodyType = BodyType.TEXT;
    return this;
  }

  public EmailMessageBuilder addTo(final String address) {
    return this.addRecipient(address, this.to);
  }

  public EmailMessageBuilder addTo(final Collection<String> address) {
    return this.addRecipients(address, this.to);
  }

  public EmailMessageBuilder addCc(final String address) {
    return this.addRecipient(address, this.cc);
  }

  public EmailMessageBuilder addCc(final Collection<String> address) {
    return this.addRecipients(address, this.cc);
  }

  public EmailMessageBuilder addBcc(final String address) {
    return this.addRecipient(address, this.bcc);
  }

  public EmailMessageBuilder addBcc(final Collection<String> address) {
    return this.addRecipients(address, this.bcc);
  }

  private EmailMessageBuilder addRecipient(final String address, final Set<String> recipientsList) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(address));
    recipientsList.add(address);
    return this;
  }

  private EmailMessageBuilder addRecipients(final Collection<String> addressList, final Set<String> recipientsList) {
    for (final String a : addressList) {
      this.addRecipient(a, recipientsList);
    }
    return this;
  }

  public Message build() throws UnsupportedEncodingException, MessagingException {
    Preconditions.checkState(this.body != null && !this.body.isEmpty());

    final Properties props = new Properties();
    final Session session = Session.getDefaultInstance(props, null);
    final Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.sender"), "The Growbit Team"));

    for (final String to : this.to) {
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    }
    for (final String cc : this.cc) {
      msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
    }
    for (final String bcc : this.bcc) {
      msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
    }

    msg.setReplyTo(new Address[]{new InternetAddress(Settings.getConfig().getString("io.growingabit.mail.replyTo"))});

    msg.setSubject(this.subject);
    switch (this.bodyType) {
      default:
      case TEXT:
        msg.setText(this.body);
        break;
      case HTML:
        final Multipart mp = new MimeMultipart();
        final MimeBodyPart htmlPart = new MimeBodyPart();
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

