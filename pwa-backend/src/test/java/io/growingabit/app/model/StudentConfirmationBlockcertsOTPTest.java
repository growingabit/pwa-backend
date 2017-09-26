package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class StudentConfirmationBlockcertsOTPTest {

  private StudentConfirmationBlockcertsOTP s;
  private final String host = "http://localhost";

  @Before
  public void setup() {
    this.s = new StudentConfirmationBlockcertsOTP(this.host);
  }


  @Test(expected = IllegalArgumentException.class)
  public void nullHost() {
    new StudentConfirmationBlockcertsOTP(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHost() {
    new StudentConfirmationBlockcertsOTP("");
  }

  @Test
  public void tsExpirationIsFuture() {
    assertThat(this.s.getTsExpiration()).isGreaterThan(new DateTime().getMillis());
  }

  @Test
  public void otpIsNotNull() {
    assertThat(this.s.getNonce()).isNotNull();
  }

  @Test
  public void otpIsNotEmpty() {
    assertThat(this.s.getNonce()).isNotEmpty();
  }

  @Test
  public void otpLength() {
    assertThat(this.s.getNonce().length()).isEqualTo(6);
  }

}
