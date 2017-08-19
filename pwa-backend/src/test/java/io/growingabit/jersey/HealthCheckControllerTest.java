package io.growingabit.jersey;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.truth.Truth;
import io.growingabit.jersey.controllers.HealthCheckController;
import org.junit.Test;

public class HealthCheckControllerTest {

  @Test()
  public void doNotThrowExceptionTest() {
    HealthCheckController controller = new HealthCheckController();
    try {
      controller.healthCheck();
      assert(true);
    }catch (Throwable e){
      assert(false);
    }
  }

}
