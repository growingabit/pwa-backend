package io.growingabit.app.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import io.growingabit.common.model.BaseModel;
import io.growingabit.common.utils.ReferenceGetter;
import io.gsonfire.annotations.ExposeMethodResult;
import java.util.LinkedList;
import java.util.List;

@Entity
@Cache
public class User extends BaseModel {

  @Id
  String id;
  @Load
  List<Ref<SignupStage>> signupStages;

  public User() {
    this.signupStages = new LinkedList<>();
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public List<SignupStage> getSignupStages() {
    return ReferenceGetter.deref(this.signupStages);
  }

  public void addSignupStage(final SignupStage stage) {
    this.signupStages.add(Ref.create(stage));
  }

  @ExposeMethodResult("signupDone")
  public boolean isSignupDone() {
    final List<SignupStage> signupStages = this.getSignupStages();
    for (final SignupStage signupStage : signupStages) {
      if (!signupStage.isDone()) {
        return false;
      }
    }
    return true;
  }
}
