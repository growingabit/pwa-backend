package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SignupStageTest {

  @Test
  public void isNotDoneByDefault() {
    assertThat(new DummySignupStage().isDone()).isFalse();
  }

  private class DummySignupStage extends SignupStage {

  }
}
