package io.growingabit.app.utils;

import java.net.URL;

public class ResourceFetcher {

  public URL fetchResource(final String name) {
    return ResourceFetcher.class.getClassLoader().getResource(name);
  }

}
