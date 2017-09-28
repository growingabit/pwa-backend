package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestUtilsTest {

  private static final String ORIGIN = "https://localhost";
  private static final String HOST = "localhost";

  @Test
  public void useOriginIfAvailable() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader("Origin")).thenReturn(ORIGIN);
    final String origin = RequestUtils.getOrigin(request);
    assertThat(origin).isEqualTo(ORIGIN);
  }

  @Test
  public void useHostIfOriginIsNull() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader("Origin")).thenReturn(null);
    Mockito.when(request.getHeader("Host")).thenReturn(HOST);
    final String origin = RequestUtils.getOrigin(request);
    assertThat(origin).isEqualTo("https://" + HOST);
  }

  @Test
  public void useHostIfOriginIsEmpty() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader("Origin")).thenReturn("");
    Mockito.when(request.getHeader("Host")).thenReturn(HOST);
    final String origin = RequestUtils.getOrigin(request);
    assertThat(origin).isEqualTo("https://" + HOST);
  }

  @Test
  public void doNotDuplicateScheme() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader("Origin")).thenReturn(ORIGIN);
    final String origin = RequestUtils.getOrigin(request);
    assertThat(origin).startsWith("https");
    assertThat(StringUtils.countMatches(origin, "https")).isAtMost(1);
  }

  @Test
  public void alwaysAddSchemeifMissing() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader("Origin")).thenReturn(null);
    Mockito.when(request.getHeader("Host")).thenReturn(HOST);
    final String origin = RequestUtils.getOrigin(request);
    assertThat(origin).startsWith("https");
    assertThat(StringUtils.countMatches(origin, "https")).isAtMost(1);
  }

}
