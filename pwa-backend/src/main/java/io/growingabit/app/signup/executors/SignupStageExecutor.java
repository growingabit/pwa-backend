package io.growingabit.app.signup.executors;

import com.google.common.base.Preconditions;
import io.growingabit.app.dao.StudentDataSignupStageDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.Settings;

public class SignupStageExecutor {

  private final User currentuser;

  public SignupStageExecutor(final User currentuser) {
    Preconditions.checkNotNull(currentuser);
    this.currentuser = currentuser;
  }

  public void exec(final InvitationCodeSignupStage stage) throws SignupStageExecutionException {
    new InvitationCodeSignupStageExecutor().exec(stage, this.currentuser);
  }

  public void exec(final StudentDataSignupStage stage) throws SignupStageExecutionException {
    try {
      Preconditions.checkNotNull(stage);
      final String signupStageIndentifier = Settings.getConfig().getString(StudentDataSignupStage.class.getCanonicalName());
      final StudentDataSignupStage userSignupStage = (StudentDataSignupStage) this.currentuser.getSignupStages().get(signupStageIndentifier).get();
      final StudentData data = new StudentData(stage.getData());
      userSignupStage.setData(data);
      userSignupStage.setDone();
      new StudentDataSignupStageDao().persist(userSignupStage);
    } catch (final IllegalArgumentException e) {
      throw new SignupStageExecutionException("Date is invalid", e);
    }
  }

}
