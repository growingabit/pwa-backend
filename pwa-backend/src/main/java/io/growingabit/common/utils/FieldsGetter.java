package io.growingabit.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldsGetter {

  public static List<Field> getAll(final Class<?> type) {
    final List<Field> result = new ArrayList<>();

    Class<?> i = type;
    Field[] fields;
    while (i != null && i != Object.class) {
      fields = i.getDeclaredFields();
      for (final Field field : fields) {
        // Avoid compiler-created fields
        if (!field.isSynthetic()) {
          result.add(field);
        }
      }
      i = i.getSuperclass();
    }

    return result;
  }

}
