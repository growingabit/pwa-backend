package io.growingabit.testUtils;

import com.googlecode.objectify.Ref;
import com.openpojo.random.RandomGenerator;
import java.util.ArrayList;
import java.util.Collection;

public class RefGenerator implements RandomGenerator {

  @Override
  public Collection<Class<?>> getTypes() {
    final ArrayList<Class<?>> list = new ArrayList<>();
    list.add(Ref.class);
    return list;
  }

  @Override
  public Object doGenerate(final Class<?> aClass) {
    try {
      return Ref.create(aClass.newInstance());
    } catch (final Exception e) {
      return null;
    }
  }
}
