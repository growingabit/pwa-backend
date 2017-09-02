package io.growingabit.app.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import io.growingabit.common.model.BaseModel;

@Entity
@Cache
public abstract class SignupStage<T extends BaseModel> extends BaseModel {

  @Id
  private Long id;
  @Parent
  Key<User> user;
  private boolean isDone;
  private T data;
  // TODO: missing stage identifie

  public SignupStage() {
    super();
    this.isDone = false;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Key<User> getUser() {
    return this.user;
  }

  public void setUser(final Key<User> user) {
    this.user = user;
  }

  public boolean isDone() {
    return this.isDone;
  }

  public void setDone(final boolean done) {
    this.isDone = done;
  }

  public T getData() {
    return this.data;
  }

  public void setData(final T data) {
    this.data = data;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SignupStage<?> that = (SignupStage<?>) o;
    return isDone() == that.isDone() &&
        Objects.equal(getId(), that.getId()) &&
        Objects.equal(getUser(), that.getUser()) &&
        Objects.equal(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), getUser(), isDone(), getData());
  }
}
