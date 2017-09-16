package io.growingabit.jersey.controllers;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class HealthCheckControllerIT {

  @Test
  public void HealthCheckReturn200() {
    when().get("/healthcheck/jersey").then().statusCode(200).body(containsString("Jersey is alive"));
  }

}
