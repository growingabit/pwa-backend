package io.growingabit.app.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.common.model.BaseModel;
import io.gsonfire.annotations.ExposeMethodResult;

@Entity
@Cache
public class User extends BaseModel {

  @Id
  transient String id;
  @Load
  Map<String, Ref<SignupStage>> signupStages;
  @Load
  Map<String, Ref<SignupStage>> mandatorySignupStages;

  public User() {
    this.signupStages = new HashMap<>();
    this.mandatorySignupStages = new HashMap<>();
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    if (StringUtils.isNotEmpty(id)) {
      this.id = id;
    }
  }

  public Map<String, Ref<SignupStage>> getSignupStages() {
    return Collections.unmodifiableMap(this.signupStages);
  }

  public Map<String, Ref<SignupStage>> getMandatorySignupStages() {
    return Collections.unmodifiableMap(this.mandatorySignupStages);
  }

  public void addSignupStage(final SignupStage stage) {
    this.signupStages.put(stage.getStageIdentifier(), Ref.create(stage));
  }

  public void addMandatorySignupStage(final SignupStage stage) {
    this.mandatorySignupStages.put(stage.getStageIdentifier(), Ref.create(stage));
  }

  @ExposeMethodResult("signupDone")
  public boolean isSignupDone() {

    // mandatory signup stages take precedences over all others
    Map<String, Ref<SignupStage>> signupStages = this.getMandatorySignupStages();
    for (final Ref<SignupStage> signupStage : signupStages.values()) {
      if (!signupStage.get().isDone()) {
        return false;
      }
    }

    signupStages = this.getSignupStages();
    for (final Ref<SignupStage> signupStage : signupStages.values()) {
      if (!signupStage.get().isDone()) {
        return false;
      }
    }
    return true;
  }

  public <T extends SignupStage> T getStage(final Class<T> signupStageClass) {
    T stage = this.getStage(signupStageClass, this.mandatorySignupStages);
    if (stage == null) {
      stage = this.getStage(signupStageClass, this.signupStages);
    }
    return stage;
  }

  private <T extends SignupStage> T getStage(final Class<T> signupStageClass, final Map<String, Ref<SignupStage>> stages) {
    final String signupStageIdentifier = Settings.getConfig().getString(signupStageClass.getCanonicalName());
    if (StringUtils.isNotEmpty(signupStageIdentifier)) {
      final Ref<SignupStage> stage = stages.get(signupStageIdentifier);
      if (stage != null) {
        return (T) stage.get();
      }
    }
    return null;
  }

}
