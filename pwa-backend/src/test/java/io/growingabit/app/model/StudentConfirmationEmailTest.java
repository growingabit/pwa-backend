package io.growingabit.app.model;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StudentConfirmationEmailTest {

  private StudentConfirmationEmail s;

  @Before
  public void setup() {
    this.s = new StudentConfirmationEmail("email@example.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalideEmail() {
    new StudentConfirmationEmail("email");
  }

  @Test
  public void tsExpirationIsFuture() {
    Assert.assertTrue(this.s.getTsExpiration() > new DateTime().getMillis());
  }

  @Test
  public void verificationCodeIsNotNull() {
    Assert.assertTrue(this.s.getVerificationCode() != null);
  }

  @Test
  public void verificationCodeIsNotEmpty() {
    Assert.assertTrue(this.s.getVerificationCode().length() > 0);
  }



}
