package io.growingabit.mail;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;

import io.growingabit.app.utils.Settings;

public class EmailMessageBuilderTest {

  private LinkedList<InternetAddress> to;
  private LinkedList<InternetAddress> cc;
  private LinkedList<InternetAddress> bcc;
  private String subject;
  private String textBody;
  private String htmlBody;

  @Before
  public void setup() throws AddressException {
    this.to = new LinkedList<>();
    this.to.add(new InternetAddress("to1@gmail.com"));
    this.to.add(new InternetAddress("to2@gmail.com"));
    this.to.add(new InternetAddress("to3@gmail.com"));

    this.cc = new LinkedList<>();
    this.cc.add(new InternetAddress("cc1@gmail.com"));
    this.cc.add(new InternetAddress("cc2@gmail.com"));
    this.cc.add(new InternetAddress("cc3@gmail.com"));

    this.bcc = new LinkedList<>();
    this.bcc.add(new InternetAddress("bcc1@gmail.com"));
    this.bcc.add(new InternetAddress("bcc2@gmail.com"));
    this.bcc.add(new InternetAddress("bcc3@gmail.com"));

    this.subject = "subject";

    this.textBody = "message";
    this.htmlBody = "<p>message<p>";
  }

  @Test(expected = IllegalArgumentException.class)
  public void fieldToIsRequired() {
    new EmailMessageBuilder(null, this.subject);
  }

  @Test(expected = IllegalArgumentException.class)
  public void doNotAcceptEmptyTo() {
    new EmailMessageBuilder("", this.subject);
  }

  @Test(expected = IllegalArgumentException.class)
  public void doNotAcceptInvalidTo() {
    new EmailMessageBuilder("an invalid email", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fieldSubjectIsRequired() {
    new EmailMessageBuilder(this.to.getFirst().toString(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void doNotAcceptEmptySubject() {
    new EmailMessageBuilder(this.to.getFirst().toString(), "");
  }

  @Test
  public void subjectIsCorrectlySetted() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .build();

    assertThat(message.getSubject()).isEqualTo(this.subject);
  }

  @Test
  public void toIsCorrectlySetted() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .build();
    assertThat(message.getRecipients(RecipientType.TO)).hasLength(1);
    assertThat(message.getRecipients(RecipientType.TO)[0].toString()).isEqualTo(this.to.getFirst().toString());
  }

  @Test
  public void canAddMoreTo() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addTo(this.to.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.TO)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.TO)).asList().containsAnyIn(this.to);
  }

  @Test
  public void doNotAddDuplicateTo() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addTo(this.to.getLast().toString())
        .addTo(this.to.getFirst().toString())
        .addTo(this.to.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.TO)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.TO)).asList().containsNoDuplicates();
  }

  @Test
  public void addAllTo() throws UnsupportedEncodingException, MessagingException {
    final LinkedList<String> toAsStrings = new LinkedList<>();
    final Iterator<InternetAddress> it = this.to.iterator();
    while (it.hasNext()) {
      toAsStrings.add(it.next().toString());
    }

    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addTo(toAsStrings)
        .build();
    assertThat(message.getRecipients(RecipientType.TO)).hasLength(3);
    assertThat(message.getRecipients(RecipientType.TO)).asList().containsAllIn(this.to);
  }

  @Test
  public void ccIsCorrectlySetted() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addCc(this.cc.getFirst().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.CC)).hasLength(1);
    assertThat(message.getRecipients(RecipientType.CC)[0].toString()).isEqualTo(this.cc.getFirst().toString());
  }

  @Test
  public void canAddMoreCC() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addCc(this.cc.getFirst().toString())
        .addCc(this.cc.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.CC)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.CC)).asList().containsAnyIn(this.cc);
  }

  @Test
  public void doNotAddDuplicateCC() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addCc(this.cc.getFirst().toString())
        .addCc(this.cc.getLast().toString())
        .addCc(this.cc.getFirst().toString())
        .addCc(this.cc.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.CC)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.TO)).asList().containsNoDuplicates();
  }

  @Test
  public void addAllCC() throws UnsupportedEncodingException, MessagingException {

    final LinkedList<String> ccAsStrings = new LinkedList<>();
    final Iterator<InternetAddress> it = this.cc.iterator();
    while (it.hasNext()) {
      ccAsStrings.add(it.next().toString());
    }

    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addCc(ccAsStrings)
        .build();
    assertThat(message.getRecipients(RecipientType.CC)).hasLength(3);
    assertThat(message.getRecipients(RecipientType.CC)).asList().containsAllIn(this.cc);
  }

  @Test
  public void bccIsCorrectlySetted() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addBcc(this.bcc.getFirst().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.BCC)).hasLength(1);
    assertThat(message.getRecipients(RecipientType.BCC)[0].toString()).isEqualTo(this.bcc.getFirst().toString());
  }

  @Test
  public void canAddMoreBCC() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addBcc(this.bcc.getFirst().toString())
        .addBcc(this.bcc.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.BCC)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.BCC)).asList().containsAnyIn(this.bcc);
  }

  @Test
  public void doNotAddDuplicateBCC() throws UnsupportedEncodingException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addBcc(this.bcc.getFirst().toString())
        .addBcc(this.bcc.getLast().toString())
        .addBcc(this.bcc.getFirst().toString())
        .addBcc(this.bcc.getLast().toString())
        .build();
    assertThat(message.getRecipients(RecipientType.BCC)).hasLength(2);
    assertThat(message.getRecipients(RecipientType.TO)).asList().containsNoDuplicates();
  }

  @Test
  public void addAllBCC() throws UnsupportedEncodingException, MessagingException {
    final LinkedList<String> bccAsStrings = new LinkedList<>();
    final Iterator<InternetAddress> it = this.bcc.iterator();
    while (it.hasNext()) {
      bccAsStrings.add(it.next().toString());
    }

    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .addBcc(bccAsStrings)
        .build();
    assertThat(message.getRecipients(RecipientType.BCC)).hasLength(3);
    assertThat(message.getRecipients(RecipientType.BCC)).asList().containsAllIn(this.bcc);
  }

  @Test(expected = IllegalStateException.class)
  public void bodyIsRequired() throws UnsupportedEncodingException, MessagingException {
    new EmailMessageBuilder(this.to.getFirst().toString(), this.subject).build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAcceptEmptyTextBody() throws UnsupportedEncodingException, MessagingException {
    new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody("")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAcceptEmptyHTMLBody() throws UnsupportedEncodingException, MessagingException {
    new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withHtmlBody("")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAcceptNullTextBody() throws UnsupportedEncodingException, MessagingException {
    new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(null)
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAcceptNullHTMLBody() throws UnsupportedEncodingException, MessagingException {
    new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withHtmlBody(null)
        .build();
  }

  @Test
  public void textBodyisCorrectlySetted() throws IOException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withTextBody(this.textBody)
        .build();

    assertThat(message.getContent()).isEqualTo(this.textBody);
  }

  @Test
  public void HTMLBodyisCorrectlySetted() throws IOException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withHtmlBody(this.htmlBody)
        .build();

    assertThat(((Multipart) message.getContent()).getBodyPart(0).getContent()).isEqualTo(this.htmlBody);
  }

  @Test
  public void fromIsCorrectlySetted() throws IOException, MessagingException {
    final Message message = new EmailMessageBuilder(this.to.getFirst().toString(), this.subject)
        .withHtmlBody(this.htmlBody)
        .build();

    assertThat(message.getReplyTo()).hasLength(1);
    assertThat(message.getReplyTo()[0].toString()).isEqualTo(Settings.getConfig().getString("io.growingabit.mail.replyTo"));
  }

}
