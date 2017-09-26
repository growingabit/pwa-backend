package io.growingabit.app.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import io.growingabit.app.exceptions.SignupStageExecutionException;

public class StudentConfirmationBlockcerts {

  private String nonce;
  private String bitcoinAddress;
  private String userId;
  private Long tsExpiration;
  private String origin;

  public StudentConfirmationBlockcerts(String origin, String userId) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(origin));
    Preconditions.checkArgument(StringUtils.isNotEmpty(userId));
    this.userId = userId;
    this.tsExpiration = new DateTime().plusDays(7).getMillis();
    this.origin = origin;
    this.nonce = this.generateBlockcertsNonce();
  }

  @SuppressWarnings("unused")
  private StudentConfirmationBlockcerts() {
    // only used by Objectify to instantiate the object
  }

  public String getNonce() {
    return this.nonce;
  }

  public String getBitcoinAddress() {
    return this.bitcoinAddress;
  }

  public void setBitcoinAddress(String bitcoinAddress) {
    this.bitcoinAddress = bitcoinAddress;
  }

  public String getUserId() {
    return this.userId;
  }

  public Long getTsExpiration() {
    return this.tsExpiration;
  }

  public String getOrigin() {
    return this.origin;
  }

  public void invalidNonce() {
    this.nonce = null;
  }

  private String generateBlockcertsNonce() {
    try {
      String hash = StringUtils.left(new String(DigestUtils.sha1(this.userId + this.tsExpiration + this.origin), "utf-8"), 10);
      return Base64.encodeBase64URLSafeString(Joiner.on(":").join(this.userId, hash).getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      throw new SignupStageExecutionException(e);
    }
  }
}
