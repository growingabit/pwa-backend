package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;
import io.gsonfire.annotations.ExposeMethodResult;
import org.junit.Test;

public class GsonFactoryTest {

  @Test
  public void gsonInstanceShouldSerializeNullFields(){
    ModelWithANullableField model = new ModelWithANullableField();
    String expected = "{\"property\":null}";
    assertThat(GsonFactory.getGsonInstance().toJson(model)).isEqualTo(expected);
  }

  @Test
  public void gsonInstanceShouldSerializeExposedMethods(){
    ExposedMethodModel model = new ExposedMethodModel();
    String expected = "{\"exposedMethod\":\"exposedMethod\"}";
    assertThat(GsonFactory.getGsonInstance().toJson(model)).isEqualTo(expected);
  }

  private class ModelWithANullableField{

    private String property;

    public String getProperty() {
      return property;
    }

    public void setProperty(String property) {
      this.property = property;
    }

  }

  private class ExposedMethodModel{

    @ExposeMethodResult("exposedMethod")
    public String exposedMethod(){
      return "exposedMethod";
    }

  }

}
