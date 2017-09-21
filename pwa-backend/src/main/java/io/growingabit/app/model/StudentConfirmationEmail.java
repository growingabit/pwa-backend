package io.growingabit.app.model;

import com.google.common.base.Preconditions;
import io.growingabit.app.utils.SecureStringGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

public class StudentConfirmationEmail {

  private static final int VERIFICATION_CODE_LENGTH = 30;

  private String email;
  transient String verificationCode;
  transient Long tsExpiration;
  transient String originHost;

  public StudentConfirmationEmail(final String email, final String originHost) {
    Preconditions.checkArgument(EmailValidator.getInstance().isValid(email));
    Preconditions.checkArgument(StringUtils.isNotEmpty(originHost));
    this.originHost = originHost;
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

  public String getOriginHost() {
    return this.originHost;
  }

  private static String generateVerificationCode() {
    return new SecureStringGenerator(VERIFICATION_CODE_LENGTH).nextString();
  }
}
