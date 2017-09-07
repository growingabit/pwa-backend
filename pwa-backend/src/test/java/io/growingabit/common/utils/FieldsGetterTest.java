package io.growingabit.common.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class FieldsGetterTest {

  @Test
  public void getFieldsFromFather() {
    assertThat(FieldsGetter.getAll(Child.class)).hasSize(6);
  }

  @Test
  public void return0IfEmpty() {
    assertThat(FieldsGetter.getAll(EmptyChild.class)).hasSize(0);
  }

  public class Father {

    public String fatherPublicField;
    protected String fatherProtectedField;
    private String fatherPrivateField;
  }

  public class Child extends Father {

    public String childPublicField;
    protected String childprotectedField;
    private String childprivateField;
  }

  public class EmptyFather {

  }

  public class EmptyChild extends EmptyFather {

  }

}
