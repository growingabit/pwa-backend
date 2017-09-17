package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
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

  @Test(expected = IllegalArgumentException.class)
  public void nullEmail() {
    new StudentConfirmationEmail(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyEmail() {
    new StudentConfirmationEmail("");
  }

  @Test
  public void tsExpirationIsFuture() {
    assertThat(this.s.getTsExpiration()).isGreaterThan(new DateTime().getMillis());
  }

  @Test
  public void verificationCodeIsNotNull() {
    assertThat(this.s.getVerificationCode()).isNotNull();
  }

  @Test
  public void verificationCodeIsNotEmpty() {
    assertThat(this.s.getVerificationCode()).isNotEmpty();
  }



}
