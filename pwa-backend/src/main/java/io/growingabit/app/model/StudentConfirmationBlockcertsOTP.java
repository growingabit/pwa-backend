package io.growingabit.app.model;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import io.growingabit.app.utils.SecureStringGenerator;

public class StudentConfirmationBlockcertsOTP {

  private static final int BLOCKCERTS_OTP_LENGTH = 6;

  private String nonce;
  private String bitcoinAddress;
  transient Long tsExpiration;
  transient String originHost;

  public StudentConfirmationBlockcertsOTP(String originHost) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(originHost));
    this.originHost = originHost;
    this.nonce = generateBlockcertsOTP();
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
  }

  @SuppressWarnings("unused")
  private StudentConfirmationBlockcertsOTP() {
    // only used by Objectify to instantiate the object
  }

  public String getNonce() {
    return this.nonce;
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
