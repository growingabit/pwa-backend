package io.growingabit.app.signup.executors;

import com.google.common.base.Preconditions;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;

public class SignupStageExecutor {

  private final User currentuser;

  public SignupStageExecutor(final User currentuser) {
    Preconditions.checkNotNull(currentuser);
    this.currentuser = currentuser;
  }

  public void exec(final InvitationCodeSignupStage stage) throws SignupStageExecutionException {
    new InvitationCodeSignupStageExecutor().exec(stage, this.currentuser);
  }

}
