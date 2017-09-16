package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StudentConfirmationPhoneTest {

  private StudentConfirmationPhone confirmationPhone;
  private static final String HOST = "http://www.example.com";

  @Before
  public void setup() {
    this.confirmationPhone = new StudentConfirmationPhone("+15005550006", HOST);
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void invalideNumber() {
    new StudentConfirmationPhone("an invalid number", HOST);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNumber() {
    new StudentConfirmationPhone(null, HOST);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyNumber() {
    new StudentConfirmationPhone("", HOST);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullHost() {
    new StudentConfirmationPhone("+15005550006", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHost() {
    new StudentConfirmationPhone("+15005550006", "");
  }

  @Test
  public void tsExpirationIsFuture() {
    assertThat(this.confirmationPhone.getTsExpiration()).isGreaterThan(new DateTime().getMillis());
  }

  @Test
  public void verificationCodeIsNotNull() {
    assertThat(this.confirmationPhone.getVerificationCode()).isNotNull();
  }

  @Test
  public void verificationCodeIsNotEmpty() {
    assertThat(this.confirmationPhone.getVerificationCode()).isNotEmpty();
  }

}
