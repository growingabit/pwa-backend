package io.growingabit.app.model.base;

public abstract class EmbeddedEntitySignupStage<T> extends SignupStage<T> {

  protected T data;

  @Override
  public T getData() {
    return this.data;
  }

  @Override
  public void setData(final T data) {
    if (data != null) {
      this.data = data;
    }
  }

  @Override
  public boolean equals(final Object o) {
    final boolean superEquals = super.equals(o);
    if (superEquals) {
      final EmbeddedEntitySignupStage signupStage = (EmbeddedEntitySignupStage) o;
      if (this.data == null) {
        return signupStage.getData() == null;
      } else {
        return this.data.equals(signupStage.getData());
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
