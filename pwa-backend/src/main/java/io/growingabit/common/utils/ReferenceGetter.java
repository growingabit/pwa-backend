package io.growingabit.common.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;
import java.util.List;

public class ReferenceGetter {

  private static class Dereferences<T> implements Function<Ref<T>, T> {

    @Override
    public T apply(final Ref<T> ref) {
      return getReference(ref);
    }
  }

  public static <T> T getReference(final Ref<T> ref) {
    return ref == null ? null : ref.get();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> List<T> deref(final List<Ref<T>> reflist) {
    return Lists.transform(reflist, new Dereferences<T>());
  }
}
