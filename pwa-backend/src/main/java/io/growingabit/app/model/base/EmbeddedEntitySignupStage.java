package io.growingabit.app.model.base;

public abstract class EmbeddedEntitySignupStage<T> extends SignupStage<T> {

  @Override
  public boolean equals(final Object o) {
    final boolean superEquals = super.equals(o);
    if (superEquals) {
      final EmbeddedEntitySignupStage signupStage = (EmbeddedEntitySignupStage) o;
      if (this.getData() == null) {
        return signupStage.getData() == null;
      } else {
        return this.getData().equals(signupStage.getData());
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
