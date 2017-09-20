package io.growingabit.app.model;

import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationEmail {

  private static final int VERIFICATION_CODE_LENGTH = 30;

  private String email;
  transient String verificationCode;
  transient Long tsExpiration;
  transient String originHost;

  public StudentConfirmationEmail(String email, String originHost) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(email));
    this.email = email;
    this.verificationCode = generateVerificationCode();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
    this.setOriginHost(originHost);
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

  public String getOriginHost() {
    return this.originHost;
  }

  public void setOriginHost(String originHost) {
    Preconditions.checkArgument(originHost != null);
    this.originHost = originHost;
  }

  public static String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextString();
  }
}
