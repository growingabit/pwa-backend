package io.growingabit.mail;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import io.growingabit.testUtils.BaseGaeTest;

public class MailGaeApiTest extends BaseGaeTest {

  @Test
  public void bodyHtmlMessageIsEqualThenTheOriginal() {
    String htmlBody = "htmlBody";

    try {
      Message message = new EmailMessageBuilder("test@example.com", "subject").withHtmlBody(htmlBody).build();
      Message msg = MailGaeApi.sendEmail(message);
      MimeMultipart content = (MimeMultipart) msg.getContent();

      for (int i = 0; i < content.getCount(); i++) {
        BodyPart bodyPart = content.getBodyPart(i);
        if (bodyPart.isMimeType(MediaType.TEXT_HTML)) {
          String body = IOUtils.toString(bodyPart.getInputStream(), "utf-8");
          assertThat(htmlBody).isEqualTo(body);
        }
      }

    } catch (MessagingException | IOException e) {
      Assert.fail();
    }
  }

  @Test
  public void bodyTextMessageIsEqualThenTheOriginal() {
    try {
      String textBody = "textBody";
      Message message = new EmailMessageBuilder("test@example.com", "subject").withTextBody(textBody).build();
      Message msg = MailGaeApi.sendEmail(message);
      assertThat(textBody).isEqualTo(msg.getContent().toString());
    } catch (MessagingException | IOException e) {
      Assert.fail();
    }

  }

}
