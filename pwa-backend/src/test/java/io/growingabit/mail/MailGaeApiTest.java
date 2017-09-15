package io.growingabit.mail;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import io.growingabit.testUtils.BaseDatastoreTest;

public class MailGaeApiTest extends BaseDatastoreTest {

  @Test
  public void bodyHtmlMessageIsEqualThenTheOriginal() {
    MailObject mailObject = new MailObject.Builder("test@example.com", "subject").withHtmlBody("htmlBody").build();

    try {
      Message msg = MailGaeApi.sendEmail(mailObject);
      MimeMultipart content = (MimeMultipart) msg.getContent();

      for (int i = 0; i < content.getCount(); i++) {
        BodyPart bodyPart = content.getBodyPart(i);
        if (bodyPart.isMimeType(MediaType.TEXT_HTML)) {
          String body = IOUtils.toString(bodyPart.getInputStream(), "utf-8");
          Assert.assertEquals(mailObject.getBody(), body);
        }
      }

    } catch (MessagingException | IOException e) {
      Assert.fail();
    }
  }

  @Test
  public void bodyTextMessageIsEqualThenTheOriginal() {
    MailObject mailObject = new MailObject.Builder("test@example.com", "subject").withTextBody("text plain").build();

    try {
      Message msg = MailGaeApi.sendEmail(mailObject);
      Assert.assertEquals(mailObject.getBody(), msg.getContent().toString());
    } catch (MessagingException | IOException e) {
      Assert.fail();
    }

  }

}
