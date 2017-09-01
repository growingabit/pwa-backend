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
  public void checkNotNullRequiredFieldsTest() {
    DummyModel dummyModel = new DummyModel();
    dummyModel.setRequiredField(new Random().toString());
    try {
      ObjectifyUtils.checkRequiredFields(dummyModel);
      assertThat(dummyModel.getRequiredField()).isNotNull();
    } catch (IllegalArgumentException | IllegalAccessException | IllegalStateException e) {
      assert (false);
    }
  }

  @Test(expected = IllegalStateException.class)
  public void checkNullRequiredFieldsTest() {
    DummyModel dummyModel = new DummyModel();
    try {
      ObjectifyUtils.checkRequiredFields(dummyModel);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      assert (false);
    }
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
