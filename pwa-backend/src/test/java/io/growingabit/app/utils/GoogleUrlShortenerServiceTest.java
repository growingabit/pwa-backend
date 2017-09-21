package io.growingabit.app.utils;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.google.api.services.urlshortener.Urlshortener.Url.Insert;
import com.google.api.services.urlshortener.model.Url;

@PrepareForTest({Url.class, Insert.class, com.google.api.services.urlshortener.model.Url.class})
public class GoogleUrlShortenerServiceTest {


  @Test(expected = IllegalArgumentException.class)
  public void nullUrlToShort() {
    new GoogleUrlShortenerService().insertSafe(null);
  }

}
