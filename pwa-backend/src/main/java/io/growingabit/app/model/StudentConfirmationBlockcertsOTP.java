package io.growingabit.app.model;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationBlockcertsOTP {

  private static final int BLOCKCERTS_OTP_LENGTH = 6;

  transient String otp;
  transient Long tsExpiration;
  transient String originHost;

  public StudentConfirmationBlockcertsOTP(String originHost) {
    Preconditions.checkArgument(originHost != null);
    this.originHost = originHost;
    this.otp = generateBlockcertsOTP();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
  }

  @SuppressWarnings("unused")
  private StudentConfirmationBlockcertsOTP() {
    // only used by Objectify to instantiate the object
  }

  public String getOtp() {
    return this.otp;
  }

  public Long getTsExpiration() {
    return this.tsExpiration;
  }

  public String getOriginHost() {
    return this.originHost;
  }

  public static String generateBlockcertsOTP() {
    return new SecureStringGenerator(BLOCKCERTS_OTP_LENGTH).nextString();
  }
}
