package io.growingabit.app.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

  public static String getOrigin(HttpServletRequest req) {
    return req.getHeader("Origin") != null ? req.getHeader("Origin") : req.getHeader("Host");
  }

}
