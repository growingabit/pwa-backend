package io.growingabit.app.exceptions;

public class SignupStageExecutionException extends RuntimeException {

  public SignupStageExecutionException(final Throwable cause) {
    super(cause);
  }

  public SignupStageExecutionException(final String message) {
    super(message);
  }

  public SignupStageExecutionException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
