package io.growingabit.app.model;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class ParentConfirmationPhone {

  private static final int VERIFICATION_CODE_LENGTH = 8;

  private String phoneNumber;
  private String name;
  private String surname;
  private transient String verificationCode;
  private transient Long tsExpiration;
  private transient String host;

  public ParentConfirmationPhone(final String phoneNumber, final String host, final String name, final String surname) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(phoneNumber));
    Preconditions.checkArgument(StringUtils.isNotEmpty(host));
    Preconditions.checkArgument(StringUtils.isNotEmpty(name));
    Preconditions.checkArgument(StringUtils.isNotEmpty(surname));
    this.host = host;
    this.phoneNumber = phoneNumber;
    this.name = name;
    this.surname = surname;
    this.verificationCode = generateVerificationCode();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
  }

  @SuppressWarnings("unused")
  private ParentConfirmationPhone() {
    // only used by Objectify to instantiate the object
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getName() {
    return this.name;
  }

  public String getSurname() {
    return this.surname;
  }

  public String getVerificationCode() {
    return this.verificationCode;
  }

  public Long getTsExpiration() {
    return this.tsExpiration;
  }

  public String getHost() {
    return this.host;
  }

  private String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextNumericString();
  }

}
