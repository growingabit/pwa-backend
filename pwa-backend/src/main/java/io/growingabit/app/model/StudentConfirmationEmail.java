package io.growingabit.app.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationEmail {

  private static final int VERIFICATION_CODE_LENGTH = 30;

  private String email;
  private transient String verificationCode;
  private transient Long tsExpiration;
  private transient String origin;

  public StudentConfirmationEmail(final String email, final String origin) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(email));
    Preconditions.checkArgument(StringUtils.isNotEmpty(origin));
    this.origin = origin;
    this.email = email;
    this.verificationCode = generateVerificationCode();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
  }

  @SuppressWarnings("unused")
  private StudentConfirmationEmail() {
    // only used by Objectify to instantiate the object
  }

  public String getEmail() {
    return this.email;
  }

  public String getVerificationCode() {
    return this.verificationCode;
  }

  public Long getTsExpiration() {
    return this.tsExpiration;
  }

  public String getOrigin() {
    return this.origin;
  }

  private static String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextString();
  }

  public void invalidVerificationCode() {
    this.verificationCode = null;
  }
}
