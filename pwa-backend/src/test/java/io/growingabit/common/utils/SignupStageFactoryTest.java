package io.growingabit.common.utils;

import static com.google.common.truth.Truth.assertThat;

import io.growingabit.app.model.SignupStage;
import io.growingabit.testUtils.DummySignupStage;
import java.util.List;
import org.junit.Test;

public class SignupStageFactoryTest {

  @Test
  public void shouldReturnRegisteredSignupStages() {
    try {
      SignupStageFactory.register(DummySignupStage.class);
      final List<SignupStage> list = SignupStageFactory.getSignupStages();
      assertThat(list).hasSize(1);
      assertThat(list.get(0)).isInstanceOf(DummySignupStage.class);
    } catch (final Exception e) {
      assert false;
    }
  }

}
