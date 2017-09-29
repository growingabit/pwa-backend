package io.growingabit.common.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;

import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;

public class SignupStageFactory {

  private static final Set<Class> signupStages = new LinkedHashSet<>();
  private static final Set<Class> mandatorySignupStages = new LinkedHashSet<>();

  public static <T extends SignupStage> void register(final Class<T> stageClass) {
    signupStages.add(stageClass);
  }

  public static <T extends SignupStage> void registerMandatory(final Class<T> stageClass) {
    mandatorySignupStages.add(stageClass);
  }

  public static List<SignupStage> getSignupStages(final Key<User> userKey) throws IllegalAccessException, InstantiationException {
    return getSignupStages(signupStages, userKey);
  }

  public static List<SignupStage> getMandatorySignupStages(final Key<User> userKey) throws IllegalAccessException, InstantiationException {
    return getSignupStages(mandatorySignupStages, userKey);
  }

  private static List<SignupStage> getSignupStages(final Set<Class> signupStages, final Key<User> userKey) throws IllegalAccessException, InstantiationException {
    final List<SignupStage> list = Lists.newArrayListWithCapacity(signupStages.size());
    SignupStage signupStage;

    for (final Class<SignupStage> signupStageClass : signupStages) {
      signupStage = signupStageClass.newInstance();
      signupStage.setUser(userKey);
      list.add(signupStage);
    }

    return list;
  }

}
