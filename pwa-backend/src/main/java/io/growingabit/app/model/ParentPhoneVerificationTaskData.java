package io.growingabit.app.model;

public class ParentPhoneVerificationTaskData {

  private String verificationCode;
  private String userId;

  public String getVerificationCode() {
    return this.verificationCode;
  }

  public void setVerificationCode(final String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }
}
