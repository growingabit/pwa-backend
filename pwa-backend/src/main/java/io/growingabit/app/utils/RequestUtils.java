package io.growingabit.app.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String getOrigin(final HttpServletRequest req) {
    final String origin = StringUtils.isNotEmpty(req.getHeader("Origin")) ? req.getHeader("Origin") : req.getHeader("Host");
    return addSchemeIfMissing(origin);
  }

  public static String getHost(final HttpServletRequest req) {
    final String host = req.getHeader("Host");
    return addSchemeIfMissing(host);
  }

  private static String addSchemeIfMissing(String url) {
    if (StringUtils.isNotEmpty(url) && !url.startsWith("http://") && !url.startsWith("https://")) {
      url = "https://" + url;
    }
    return url;
  }

}
