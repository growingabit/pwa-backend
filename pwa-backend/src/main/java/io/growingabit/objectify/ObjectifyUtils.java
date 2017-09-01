package io.growingabit.objectify;

import java.lang.reflect.Field;

import io.growingabit.objectify.annotations.Required;

public class ObjectifyUtils {

  public static void checkRequiredFields(Object object) throws IllegalArgumentException, IllegalAccessException, IllegalStateException {
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Required.class)) {
        field.setAccessible(true);
        if (field.get(object) == null) {
          throw new IllegalStateException("Field " + field.getName() + " is marked as @Required but has null value");
        }
      }
    }
  }

}
