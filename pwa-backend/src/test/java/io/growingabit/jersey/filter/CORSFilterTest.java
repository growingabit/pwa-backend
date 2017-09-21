package io.growingabit.jersey.filter;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.growingabit.jersey.filters.CORSFilter;

@RunWith(MockitoJUnitRunner.class)
public class CORSFilterTest {


  private final String ORIGIN = "Access-Control-Allow-Origin";
  private final String HEADERS = "Access-Control-Allow-Headers";
  private final String CREDENTIAL = "Access-Control-Allow-Credentials";
  private final String METHODS = "Access-Control-Allow-Methods";

  @Mock
  private ContainerRequestContext request;
  @Mock
  private ContainerResponseContext response;

  private final MultivaluedMap<String, Object> fakeRespHeaderMap = new MultivaluedHashMap<>();

  private CORSFilter filter;

  @Before
  public void setUp() {
    Mockito.when(this.response.getHeaders()).thenReturn(this.fakeRespHeaderMap);
    this.filter = new CORSFilter();
  }

  @Test
  public void shouldContainsCORSOriginHeader() throws IOException {
    this.filter.filter(this.request, this.response);
    assertThat(this.response.getHeaders().get(this.ORIGIN)).contains("*");
  }

  @Test
  public void shouldContainsCORSHeadersHeader() throws IOException {
    this.filter.filter(this.request, this.response);
    // not exactly correct. Order should not count...
    assertThat(this.response.getHeaders().get(this.HEADERS)).contains("origin, content-type, accept, authorization");
  }

  @Test
  public void shouldContainsCORSCredentialsHeader() throws IOException {
    this.filter.filter(this.request, this.response);
    assertThat(this.response.getHeaders().get(this.CREDENTIAL)).contains("true");
  }

  @Test
  public void shouldContainsCORSMethodsHeader() throws IOException {
    this.filter.filter(this.request, this.response);
    // not exactly correct. Order should not count...
    assertThat(this.response.getHeaders().get(this.METHODS)).contains("GET, POST, PUT, DELETE, OPTIONS, HEAD");
  }

}
