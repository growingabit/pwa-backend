package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ParentConfirmationPhoneTest {

  private ParentConfirmationPhone confirmationPhone;
  private static final String HOST = "http://www.example.com";
  private static final String PARENT_FIRSTNAME = "firstname";
  private static final String PARENT_LASTNAME = "lastname";

  @Before
  public void setup() {
    this.confirmationPhone = new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void invalideNumber() {
    new ParentConfirmationPhone("an invalid number", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNumber() {
    new ParentConfirmationPhone(null, HOST, PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyNumber() {
    new ParentConfirmationPhone("", HOST, PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullHost() {
    new ParentConfirmationPhone("+15005550006", null, PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyHost() {
    new ParentConfirmationPhone("+15005550006", "", PARENT_FIRSTNAME, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullFirstName() {
    new ParentConfirmationPhone("+15005550006", HOST, null, PARENT_LASTNAME);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyFirstName() {
    new ParentConfirmationPhone("+15005550006", HOST, "", PARENT_LASTNAME);
  }


  @Test(expected = IllegalArgumentException.class)
  public void nullLastName() {
    new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyLastName() {
    new ParentConfirmationPhone("+15005550006", HOST, PARENT_FIRSTNAME, "");
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
