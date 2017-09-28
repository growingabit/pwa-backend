package io.growingabit.jersey.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

public class CharsetFilter implements ContainerResponseFilter {

  @Override
  public void filter(final ContainerRequestContext request, final ContainerResponseContext response) {
    final MediaType type = response.getMediaType();
    if (type != null) {
      String contentType = type.toString();
      if (!contentType.contains("charset")) {
        contentType = contentType + ";charset=utf-8";
        response.getHeaders().putSingle("Content-Type", contentType);
      }
    }
  }
}
