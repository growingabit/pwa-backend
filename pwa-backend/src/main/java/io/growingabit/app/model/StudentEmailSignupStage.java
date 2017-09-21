package io.growingabit.app.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;

@Entity
@Cache
public class StudentEmailSignupStage extends SignupStage<StudentConfirmationEmail> {

  private StudentConfirmationEmail data;

  @Override
  public StudentConfirmationEmail getData() {
    return this.data;
  }

  @Override
  public void setData(final StudentConfirmationEmail data) {
    if (data != null) {
      this.data = data;
    }
  }

  @Override
  public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {
    executor.exec(this);
  }

}
