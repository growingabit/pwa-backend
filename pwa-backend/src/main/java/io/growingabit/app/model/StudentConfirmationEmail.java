package io.growingabit.app.model;

import org.joda.time.DateTime;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationEmail {

  private static final int VERIFICATION_CODE_LENGTH = 30;

  private String email;
  private String verificationCode;
  private Long tsExpiration;

  public StudentConfirmationEmail(String email) {
    this.verificationCode = new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextString();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
    this.email = email;
  }

  @SuppressWarnings("unused")
  private StudentConfirmationEmail() {
    // only used by Objectify to instantiate the object
  }

  public String getEmail() {
    return email;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public Long getTsExpiration() {
    return tsExpiration;
  }

}
