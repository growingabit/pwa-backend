package io.growingabit.app.model.base;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;

public abstract class ReferenceSignupStage<T> extends SignupStage<T> {

  @Load
  protected Ref<T> data;

  @Override
  public T getData() {
    return this.data == null ? null : this.data.get();
  }

  @Override
  public void setData(final T data) {
    if (data != null) {
      this.data = Ref.create(data);
    }
  }

}
