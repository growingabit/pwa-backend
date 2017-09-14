package it.growbit.mail;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.growingabit.mail.MailObject;

public class MailServiceTest {

  private MailObject mailObject;

  @Before
  public void setup() {
    this.mailObject = new MailObject.Builder("to@example.com", "subject").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void toCanNotBeNull() {
    new MailObject.Builder(null, "subject").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void toCanNotBeEmpty() {
    new MailObject.Builder("", "subject").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void bodyCanNotBeNull() {
    new MailObject.Builder("to@example.com", "subject").withHtmlBody(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void bodyCanNotBeEmtpy() {
    new MailObject.Builder("to@example.com", "subject").withHtmlBody("").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void toShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addTo("emailNotValid").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void toListShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addTo(Arrays.asList("emailNotValid")).withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void ccShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addCc("emailNotValid").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void ccListShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addCc(Arrays.asList("emailNotValid")).withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void bccShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addBcc("emailNotValid").withHtmlBody("htmlBody").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void bccListShouldBeValid() {
    new MailObject.Builder("me@example.com", "subject").addBcc(Arrays.asList("emailNotValid")).withHtmlBody("htmlBody").build();
  }

  @Test
  public void bodyTypeShouldNotBeNull() {
    Assert.assertNotNull(mailObject.getBodyType());
  }

}
