package io.growingabit.backoffice.model;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.filters.FilterNonConcrete;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import io.growingabit.testUtils.EqualsHashCodeRule;
import io.growingabit.testUtils.RefGenerator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BackofficePojoTest {

  private final String packageName = "io.growingabit.backoffice.model";
  private Validator validator;

  @BeforeClass
  public static void onlyOnce() {
    RandomFactory.addRandomGenerator(new RefGenerator());
  }

  @Before
  public void setup() {
    this.validator = ValidatorBuilder.create()
        .with(new SetterTester())
        .with(new GetterTester())
        .with(new NoFieldShadowingRule())
        .with(new NoPublicFieldsExceptStaticFinalRule())
        .with(new EqualsHashCodeRule())
        .build();
  }

  @Test
  public void validatePojos() {
    this.validator.validate(this.packageName, new FilterChain(new FilterNonConcrete(), new FilterTestClasses()));
  }

  private static class FilterTestClasses implements PojoClassFilter {

    @Override
    public boolean include(final PojoClass pojoClass) {
      return !pojoClass.getSourcePath().contains("/test-classes/");
    }
  }
}

