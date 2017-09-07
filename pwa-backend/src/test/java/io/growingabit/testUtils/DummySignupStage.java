package io.growingabit.testUtils;

import com.googlecode.objectify.annotation.Entity;
import io.growingabit.app.model.base.EmbeddedEntitySignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.common.model.BaseModel;

@Entity
public class DummySignupStage extends EmbeddedEntitySignupStage<BaseModel> {

  public DummySignupStage() {
    super();
  }

  @Override
  public void exec(final SignupStageExecutor executor) {

  }
}
