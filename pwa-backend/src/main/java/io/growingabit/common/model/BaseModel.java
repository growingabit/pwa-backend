package io.growingabit.common.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import io.growingabit.objectify.ObjectifyUtils;
import io.gsonfire.annotations.ExposeMethodResult;
import org.joda.time.DateTime;

public abstract class BaseModel {

  private transient long creationDate;

  @Index
  private transient long modifiedDate;

  public BaseModel() {
    this.creationDate = -1;
  }

  public final long getCreationDate() {
    return this.creationDate;
  }

  public final long getModifiedDate() {
    return this.modifiedDate;
  }

  @ExposeMethodResult("webSafeKey")
  public final String getWebSafeKey() {
    try {
      return Key.create(this).getString();
    } catch (final Throwable throwable) {
      return null;
    }
  }

  @OnSave
  private void onSave() throws IllegalArgumentException, IllegalAccessException, NullPointerException {
    this.modifiedDate = new DateTime().getMillis();
    if (this.creationDate < 0) {
      this.creationDate = this.modifiedDate;
    }
    ObjectifyUtils.checkRequiredFields(this);
  }
}
