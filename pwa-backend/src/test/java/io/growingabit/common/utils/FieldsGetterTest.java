package io.growingabit.common.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class FieldsGetterTest {

  @Test
  public void getFieldsFromFather() {
    // Note that an inner class have an hidden field that store a reference
    // to the outher class, so for both Father an Child FieldsGetter
    // should retrieve 4 fields
    assertThat(FieldsGetter.getAll(Child.class).size()).isEqualTo(8);
  }

  private class Father {

    public String fatherPublicField;
    protected String fatherProtectedField;
    private String fatherPrivateField;
  }

  private class Child extends Father {

    public String childPublicField;
    protected String childprotectedField;
    private String childprivateField;
  }

}
