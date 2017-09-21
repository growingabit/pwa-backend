package io.growingabit.jersey;

import io.growingabit.jersey.controllers.HealthCheckController;
import org.junit.Test;

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
