package io.growingabit.sms.exceptions;

public class SMSSendingException extends RuntimeException {

  public SMSSendingException(final String message) {
    super(message);
  }

  public SMSSendingException(final Throwable cause) {
    super(cause);
  }

  public SMSSendingException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
