package io.growingabit.app.model;

import io.growingabit.app.utils.SecureStringGenerator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class StudentConfirmationPhone {

  private static final int VERIFICATION_CODE_LENGTH = 8;

  private String phoneNumber;
  private transient String verificationCode;
  private transient Long tsExpiration;

  public StudentConfirmationPhone(final String phoneNumber) {
    if (StringUtils.isEmpty(phoneNumber)) {
      throw new IllegalArgumentException("Phnoe number should not be empty or null");
    }
    this.phoneNumber = phoneNumber;
    this.verificationCode = generateVerificationCode();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
  }

  @SuppressWarnings("unused")
  private StudentConfirmationPhone() {
    // only used by Objectify to instantiate the object
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getVerificationCode() {
    return this.verificationCode;
  }

  public Long getTsExpiration() {
    return this.tsExpiration;
  }

  private String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextNumericString();
  }
}
