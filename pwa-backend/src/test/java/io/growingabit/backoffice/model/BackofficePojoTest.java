package io.growingabit.backoffice.model;

import com.googlecode.objectify.ObjectifyService;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.filters.FilterNonConcrete;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import io.growingabit.testUtils.BaseGaeTest;
import org.junit.Before;
import org.junit.Test;

public class BackofficePojoTest extends BaseGaeTest {

  private final String packageName = "io.growingabit.backoffice.model";
  private Validator validator;

  @Before
  public void setup() {
    ObjectifyService.factory().register(Invitation.class);
    this.validator = ValidatorBuilder.create()
        .with(new NoFieldShadowingRule())
        .with(new NoPublicFieldsExceptStaticFinalRule())
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

