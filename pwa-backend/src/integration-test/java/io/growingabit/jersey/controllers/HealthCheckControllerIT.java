package io.growingabit.jersey.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static com.google.common.truth.Truth.assertThat;
import javax.ws.rs.core.Response;
import org.junit.Test;

public class HealthCheckControllerIT {

  @Test
  public void HealthCheckReturn200() {
    when().get("/healthcheck/jersey").then().statusCode(200).body(containsString("Jersey is alive"));
  }

}
