package io.growingabit.common.utils;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import io.growingabit.app.dao.GenericSignupStageDao;
import io.growingabit.app.model.User;
import io.growingabit.app.model.base.SignupStage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SignupStageFactory {

  private static final Set<Class> signupStages = new HashSet<>();
  private static final Set<Class> mandatorySignupStages = new HashSet<>();
  private static final GenericSignupStageDao genericSignupStageDao = new GenericSignupStageDao();

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

    genericSignupStageDao.persist(list);
    return list;
  }

}
