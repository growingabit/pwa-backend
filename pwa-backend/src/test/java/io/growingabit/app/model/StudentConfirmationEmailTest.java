package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class StudentConfirmationEmailTest {

  private StudentConfirmationEmail s;
  private final String host = "http://localhost";

  @Before
  public void setup() {
    this.s = new StudentConfirmationEmail("email@example.com", this.host);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalideEmail() {
    new StudentConfirmationEmail("email", this.host);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullEmail() {
    new StudentConfirmationEmail(null, this.host);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullHost() {
    new StudentConfirmationEmail("email@example.com", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHost() {
    new StudentConfirmationEmail("email@example.com", "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyEmail() {
    new StudentConfirmationEmail("", this.host);
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
