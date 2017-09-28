package io.growingabit.jersey.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

@Priority(Priorities.AUTHORIZATION)
public class OriginFilter implements ContainerRequestFilter {

  @Override
  public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
    final String origin = containerRequestContext.getHeaderString("Origin");
    // Check if same origin request
    if (origin != null) {
      // check if it isn't localhost or https origin
      final String scheme = containerRequestContext.getUriInfo().getRequestUri().getScheme();
      if (!(origin.contains("localhost") || scheme.equalsIgnoreCase("https"))) {
        containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      }
    }
  }
}
