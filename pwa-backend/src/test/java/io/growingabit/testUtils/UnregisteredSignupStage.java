package io.growingabit.testUtils;

import com.googlecode.objectify.annotation.Entity;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.common.model.BaseModel;

@Entity
public class UnregisteredSignupStage extends SignupStage<BaseModel> {

  @Override
  public BaseModel getData() {
    return null;
  }

  @Override
  public void setData(final BaseModel data) {

  }

  @Override
  public void exec(final SignupStageExecutor executor) {

  }
}
