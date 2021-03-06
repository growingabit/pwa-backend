package io.growingabit.app.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;

@Entity
@Cache
public class StudentBlockcertsSignupStage extends SignupStage<StudentConfirmationBlockcerts> {

  private StudentConfirmationBlockcerts data;

  @Override
  public StudentConfirmationBlockcerts getData() {
    return this.data;
  }

  @Override
  public void setData(StudentConfirmationBlockcerts data) {
    if (data != null) {
      this.data = data;
    }
  }

  @Override
  public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {
    executor.exec(this);
  }

}
