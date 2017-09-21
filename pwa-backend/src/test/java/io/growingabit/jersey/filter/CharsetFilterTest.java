package io.growingabit.jersey.filter;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.growingabit.jersey.filters.CharsetFilter;

@RunWith(MockitoJUnitRunner.class)
public class CharsetFilterTest {

  private final String CONTENT_TYPE = "Content-Type";

  @Mock
  private ContainerRequestContext request;
  @Mock
  private ContainerResponseContext response;

  private final MultivaluedMap<String, Object> fakeRespHeaderMap = new MultivaluedHashMap<>();

  private CharsetFilter filter;

  @Before
  public void setUp() {
    Mockito.when(this.response.getHeaders()).thenReturn(this.fakeRespHeaderMap);
    this.filter = new CharsetFilter();
  }

  @Test
  public void shouldContainsCharsetHeader() throws IOException {
    Mockito.when(this.response.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
    this.filter.filter(this.request, this.response);
    assertThat(this.response.getHeaders().get(this.CONTENT_TYPE)).contains(MediaType.APPLICATION_JSON + ";charset=utf-8");
  }

}
