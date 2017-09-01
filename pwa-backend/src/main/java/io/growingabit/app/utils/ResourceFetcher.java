package io.growingabit.app.utils;

import java.io.InputStream;
import java.net.URL;

public class ResourceFetcher {

  public InputStream fetchResourceAsStream(final String name) {
    return ResourceFetcher.class.getClassLoader().getResourceAsStream(name);
  }

  public URL fetchResource(final String name) {
    return ResourceFetcher.class.getClassLoader().getResource(name);
  }

}
