package io.growingabit.common.utils;

import com.google.common.collect.Lists;
import io.growingabit.app.model.SignupStage;
import java.util.LinkedList;
import java.util.List;

public class SignupStageFactory {

  private static final List<Class> signupStages = new LinkedList<>();

  public static <T extends SignupStage> void register(final Class<T> stageClass) {
    signupStages.add(stageClass);
  }

  public static List<SignupStage> getSignupStages() throws IllegalAccessException, InstantiationException {
    final List<SignupStage> list = Lists.newArrayListWithCapacity(signupStages.size());
    for (final Class<SignupStage> signupStageClass : signupStages) {
      list.add(signupStageClass.newInstance());
    }
    return list;
  }
}
