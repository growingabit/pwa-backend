package io.growingabit.app.model;

import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.SaveException;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;
import io.growingabit.app.utils.ResourceFetcher;
import io.growingabit.app.utils.Settings;
import io.growingabit.common.model.BaseModel;
import io.growingabit.objectify.annotations.Required;
import org.apache.commons.lang3.StringUtils;

@Entity
@Cache
public abstract class SignupStage<T extends BaseModel> extends BaseModel {

  @Id
  private Long id;

  @Parent
  @Required
  Key<User> user;
  private boolean isDone;
  private T data;

  @Ignore
  private String stageIdentifier;

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

  public String getStageIdentifier() {
    return this.stageIdentifier;
  }

  @OnSave
  private void onSave() {
    if (StringUtils.isEmpty(this.stageIdentifier)) {
      this.stageIdentifier = new Settings(new ResourceFetcher()).getConfig().getString(this.getClass().getCanonicalName());
      if (StringUtils.isEmpty(this.stageIdentifier)) {
        throw new SaveException(this, "Missing " + this.getClass().getCanonicalName() + " propery on settings file", null);
      }
    }
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
