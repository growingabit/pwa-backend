package io.growingabit.app.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.gsonfire.GsonFireBuilder;

public class GsonFactory {

  public static Gson getGsonInstance() {
    return GsonFactory.getGson();
  }

  private static GsonBuilder getGsonBuilder() {
    return new GsonFireBuilder()
        .enableExposeMethodResult()
        .createGsonBuilder();
  }

  private static Gson getGson() {
    return GsonFactory.getGsonBuilder()
        .registerTypeAdapterFactory(new RefAdapterFactory())
        .serializeNulls()
        .create();
  }
}
