package io.growingabit.jersey.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/healthcheck")
public class HealthCheckController {

  @GET
  @Path("/jersey")
  @Produces("text/plain")
  public String healthCheck() {
    return "Jersey is alive";
  }

}
