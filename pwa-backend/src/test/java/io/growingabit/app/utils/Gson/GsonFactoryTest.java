package io.growingabit.app.utils.Gson;

import static com.google.common.truth.Truth.assertThat;

import io.growingabit.app.utils.gson.GsonFactory;
import io.gsonfire.annotations.ExposeMethodResult;
import org.junit.Test;

public class GsonFactoryTest {

  @Test
  public void gsonInstanceShouldSerializeNullFields() {
    final ModelWithANullableField model = new ModelWithANullableField();
    final String expected = "{\"property\":null}";
    assertThat(GsonFactory.getGsonInstance().toJson(model)).isEqualTo(expected);
  }

  @Test
  public void gsonInstanceShouldSerializeExposedMethods() {
    final ExposedMethodModel model = new ExposedMethodModel();
    final String expected = "{\"exposedMethod\":\"exposedMethod\"}";
    assertThat(GsonFactory.getGsonInstance().toJson(model)).isEqualTo(expected);
  }

  private class ModelWithANullableField {

    private String property;

    public String getProperty() {
      return this.property;
    }

    public void setProperty(final String property) {
      this.property = property;
    }

  }

  private class ExposedMethodModel {

    @ExposeMethodResult("exposedMethod")
    public String exposedMethod() {
      return "exposedMethod";
    }

  }

}
