package io.growingabit.objectify;

import java.lang.reflect.Field;
import java.util.List;

import io.growingabit.common.utils.FieldsGetter;
import io.growingabit.objectify.annotations.Required;

public class ObjectifyUtils {

  public static void checkRequiredFields(final Object object) throws IllegalArgumentException, IllegalAccessException, IllegalStateException {
    final List<Field> fields = FieldsGetter.getAll(object.getClass());
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Required.class)) {
        field.setAccessible(true);
        if (field.get(object) == null) {
          throw new IllegalStateException("Field " + field.getName() + " is marked as @Required but has null value");
        }
      }
    }
  }

}
