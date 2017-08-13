package io.growingabit.app.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.gsonfire.GsonFireBuilder;

public class GsonFactory {

  public static Gson getGsonInstance() {
    return GsonFactory.getGson();
  }

  private static GsonBuilder getGsonBuilder() {
    GsonFireBuilder gsonFireBuilder = new GsonFireBuilder();
    gsonFireBuilder.enableExposeMethodResult();
    return gsonFireBuilder.createGsonBuilder();
  }

  private static Gson getGson() {
    GsonBuilder builder = GsonFactory.getGsonBuilder();
    builder.serializeNulls();
    return builder.create();
  }
}
