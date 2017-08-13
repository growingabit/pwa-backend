package io.growingabit.jersey.providers;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.growingabit.app.utils.GsonFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

  private static final Charset charset = StandardCharsets.UTF_8;

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public long getSize(Object obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    final Type jsonType = type.equals(genericType) ? type : genericType;
    try (Writer writer = new OutputStreamWriter(entityStream, charset)) {
      GsonFactory.getGsonInstance().toJson(obj, jsonType, writer);
    } catch (JsonIOException | JsonSyntaxException ex) {
      throw new WebApplicationException(ex.getMessage(), ex, Response.Status.BAD_REQUEST);
    }
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
    final Type jsonType = type.equals(genericType) ? type : genericType;
    try (Reader reader = new InputStreamReader(entityStream, charset)) {
      return GsonFactory.getGsonInstance().fromJson(reader, jsonType);
    } catch (JsonIOException | JsonSyntaxException ex) {
      throw new WebApplicationException(ex.getMessage(), ex, Response.Status.BAD_REQUEST);
    }
  }
}
