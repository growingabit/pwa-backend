package io.growingabit.testUtils;

import io.growingabit.app.utils.Settings;
import io.growingabit.common.utils.SignupStageFactory;
import java.lang.reflect.Field;
import java.util.Collection;

public class Utils {

  public static void clearSignupStageFactory() throws NoSuchFieldException, IllegalAccessException {
    final Field signupStages = SignupStageFactory.class.getDeclaredField("signupStages");
    signupStages.setAccessible(true);
    Collection instance = (Collection) signupStages.get(null);
    instance.clear();

    final Field mandatorySignupStages = SignupStageFactory.class.getDeclaredField("mandatorySignupStages");
    mandatorySignupStages.setAccessible(true);
    instance = (Collection) mandatorySignupStages.get(null);
    instance.clear();
  }

  public static void clearSettings() throws NoSuchFieldException, IllegalAccessException {
    final Field config = Settings.class.getDeclaredField("config");
    config.setAccessible(true);
    config.set(null, null);
  }

}
