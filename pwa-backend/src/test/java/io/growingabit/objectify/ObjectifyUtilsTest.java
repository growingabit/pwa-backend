package io.growingabit.objectify;

import static com.google.common.truth.Truth.assertThat;

import java.util.Random;

import org.junit.Test;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import io.growingabit.app.model.BaseModel;
import io.growingabit.objectify.annotations.Required;

public class ObjectifyUtilsTest {

  @Test
  public void checkNotNullRequiredFieldsTest() throws IllegalArgumentException, IllegalAccessException {
    DummyModel dummyModel = new DummyModel();
    dummyModel.setRequiredField(new Random().toString());
    ObjectifyUtils.checkRequiredFields(dummyModel);
    assertThat(dummyModel.getRequiredField() != null);
  }
  
  @Test(expected = NullPointerException.class)
  public void checkNullRequiredFieldsTest() throws IllegalArgumentException, IllegalAccessException {
    DummyModel dummyModel = new DummyModel();
    ObjectifyUtils.checkRequiredFields(dummyModel);
  }

  @Entity
  private class DummyModel extends BaseModel {

    @Id
    Long id;

    @Required
    private String requiredField;

    public String getRequiredField() {
      return requiredField;
    }

    public void setRequiredField(String requiredField) {
      this.requiredField = requiredField;
    }

  }


}
