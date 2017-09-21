package io.growingabit.app.model;

import org.junit.Before;
import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;

public class AppPojoTest {

  private final String packageName = "io.growingabit.app.model";
  private Validator validator;

  @Before
  public void setup() {
    this.validator = ValidatorBuilder.create()
        .with(new NoFieldShadowingRule())
        .with(new NoPublicFieldsExceptStaticFinalRule())
        .build();
  }

  @Test
  public void validatePojos() {
    this.validator.validate(this.packageName, new FilterTestClasses());
  }

  private static class FilterTestClasses implements PojoClassFilter {

    @Override
    public boolean include(final PojoClass pojoClass) {
      return !pojoClass.getSourcePath().contains("/test-classes/");
    }
  }
}
