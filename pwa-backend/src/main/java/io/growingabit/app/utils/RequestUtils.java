package io.growingabit.app.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String getOrigin(HttpServletRequest req) {
    return StringUtils.isNotEmpty(req.getHeader("Origin")) ? req.getHeader("Origin") : req.getHeader("Host");
  }

}
