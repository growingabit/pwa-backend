package io.growingabit.app.utils;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class GoogleUrlShortenerService extends Urlshortener {

  protected static final XLogger logger = XLoggerFactory.getXLogger(GoogleUrlShortenerService.class);

  public GoogleUrlShortenerService() {
    super(new NetHttpTransport(), new JacksonFactory(), getCredential());
  }

  public String insertSafe(final String longUrl) {
    Preconditions.checkArgument(StringUtils.isNoneEmpty(longUrl));
    try {
      final com.google.api.services.urlshortener.model.Url url = new com.google.api.services.urlshortener.model.Url();
      url.setLongUrl(longUrl);
      final com.google.api.services.urlshortener.model.Url shortUrl = this.url().insert(url).execute();
      return shortUrl.getId();
    } catch (final IOException e) {
      logger.catching(e);
      return longUrl;
    }
  }

  private static AppIdentityCredential getCredential() {
    return new AppIdentityCredential(Arrays.asList(UrlshortenerScopes.URLSHORTENER));
  }
}
