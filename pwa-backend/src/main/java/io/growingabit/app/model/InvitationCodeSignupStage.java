package io.growingabit.app.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.base.ReferenceSignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.backoffice.model.Invitation;

@Entity
@Cache
public class InvitationCodeSignupStage extends ReferenceSignupStage<Invitation> {

  @Override
  public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {
    executor.exec(this);
  }

}
