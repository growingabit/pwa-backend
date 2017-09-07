package io.growingabit.app.model.base;

public abstract class EmbeddedEntitySignupStage<T> extends SignupStage<T> {

  protected T data;

  @Override
  public T getData() {
    return this.data;
  }

  @Override
  public void setData(final T data) {
    this.data = data;
  }

}
