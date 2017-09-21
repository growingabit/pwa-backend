package io.growingabit.jersey;

import org.junit.Test;

import io.growingabit.jersey.controllers.HealthCheckController;

public class HealthCheckControllerTest {

  @Test()
  public void doNotThrowExceptionTest() {
    final HealthCheckController controller = new HealthCheckController();
    try {
      controller.healthCheck();
      assert (true);
    } catch (final Throwable e) {
      assert (false);
    }
  }

}
