package io.growingabit.testUtils;

import com.googlecode.objectify.annotation.Entity;
import io.growingabit.app.model.SignupStage;
import io.growingabit.common.model.BaseModel;

@Entity
public class DummySignupStage extends SignupStage<BaseModel> {

  public DummySignupStage() {
    super();
  }
}
