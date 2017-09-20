package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StudentConfirmationPhoneTest {

  private StudentConfirmationPhone confirmationPhone;

  @Before
  public void setup() {
    this.confirmationPhone = new StudentConfirmationPhone("+15005550006");
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void invalideNumber() {
    new StudentConfirmationPhone("an invalid number");
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNumber() {
    new StudentConfirmationPhone(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyNumber() {
    new StudentConfirmationPhone("");
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
