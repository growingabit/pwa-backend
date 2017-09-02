package io.growingabit.testUtils;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.Rule;

@SuppressWarnings("WeakerAccess")
public class EqualsHashCodeRule implements Rule {

  @Override
  public void evaluate(final PojoClass pojoClass) {

    final boolean hasEquals = hasEquals(pojoClass);
    final boolean hasHashCode = hasHashCode(pojoClass);

    if (!(hasEquals && hasHashCode)) {
      Affirm.fail("Must implement both equals and hashcode in Pojo [" + pojoClass + "]");
    }

  }

  private boolean hasHashCode(final PojoClass pojoClass) {
    for (final PojoMethod method : pojoClass.getPojoMethods()) {
      if (method.getName().equals("hashCode") && method.getPojoParameters().size() == 0) {
        return true;
      }
    }
    return false;
  }

  private boolean hasEquals(final PojoClass pojoClass) {
    for (final PojoMethod method : pojoClass.getPojoMethods()) {
      if (method.getName().equals("equals") && method.getPojoParameters().size() == 1) {
        return true;
      }
    }
    return false;
  }

}
