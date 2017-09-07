package io.growingabit.app.model.base;

import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.SaveException;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Parent;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.User;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.app.utils.Settings;
import io.growingabit.common.model.BaseModel;
import io.growingabit.objectify.annotations.Required;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.lang3.StringUtils;

public abstract class SignupStage<T> extends BaseModel {

  @Id
  private Long id;

  // we can safely void serialize userKey into json
  // because this class is subclass of BaseMode
  // and the information about the user is int the
  // websafestring, as the user is the parent
  @Parent
  @Required
  transient Key<User> user;

  private boolean isDone;

  @Ignore
  transient private final String stageIdentifier;

  public SignupStage() {
    super();
    this.isDone = false;
    this.stageIdentifier = Settings.getConfig().getString(this.getClass().getCanonicalName());
    if (StringUtils.isEmpty(this.stageIdentifier)) {
      throw new SaveException(this, "Missing " + this.getClass().getCanonicalName() + " property on settings file", null);
    }
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

  public void setDone() {
    this.isDone = true;
  }

  public String getStageIdentifier() {
    return this.stageIdentifier;
  }

  // Into the user we save maps of Signup stage, so unless both
  // EmbeddedEntitySignupStage and ReferenceSignupStage have a data field,
  // Objectify does not find it, because it look only for Signup stage
  // But as both subclass provide a method to obtain data information,
  // we can serialize into the json thanks to GsonFire @ExposeMethodResult annotation
  @ExposeMethodResult("data")
  public abstract T getData();

  public abstract void setData(final T data);

  public abstract void exec(final SignupStageExecutor executor) throws SignupStageExecutionException;

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
