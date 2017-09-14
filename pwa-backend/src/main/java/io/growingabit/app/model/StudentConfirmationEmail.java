package io.growingabit.app.model;

import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationEmail {

  private static final int VERIFICATION_CODE_LENGTH = 30;

  private String email;
  private String verificationCode;
  private Long tsExpiration;

  public StudentConfirmationEmail(String email) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(email));
    this.email = email;
    this.verificationCode = generateVerificationCode();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
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

  public static String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextString();
  }
}
