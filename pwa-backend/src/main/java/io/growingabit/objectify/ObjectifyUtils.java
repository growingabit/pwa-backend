package io.growingabit.objectify;

import java.lang.reflect.Field;

import com.google.common.base.Preconditions;

import io.growingabit.objectify.annotations.Required;

public class ObjectifyUtils {

    public static void checkRequiredFields(Object object) throws IllegalArgumentException, IllegalAccessException, NullPointerException {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Required.class)) {
                field.setAccessible(true);
                Preconditions.checkNotNull(field.get(object));
            }
        }
    }

}
