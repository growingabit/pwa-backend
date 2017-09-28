package io.growingabit.jersey.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 * https://stackoverflow.com/questions/28065963/how-to-handle-cors-using-jax-rs-with-jersey
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CORSFilter implements ContainerResponseFilter {

  @Override
  public void filter(final ContainerRequestContext request, final ContainerResponseContext response) throws IOException {
    response.getHeaders().add("Access-Control-Allow-Origin", "*");
    response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    response.getHeaders().add("Access-Control-Allow-Credentials", "true");
    response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
  }
}
