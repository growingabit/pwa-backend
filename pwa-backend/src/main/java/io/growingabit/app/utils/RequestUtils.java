package io.growingabit.app.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String getOrigin(final HttpServletRequest req) {
    String origin = StringUtils.isNotEmpty(req.getHeader("Origin")) ? req.getHeader("Origin") : req.getHeader("Host");
    if (StringUtils.isNotEmpty(origin) && !origin.startsWith("http://") && !origin.startsWith("https://")) {
      origin = "https://" + origin;
    }
    return origin;
  }

}
