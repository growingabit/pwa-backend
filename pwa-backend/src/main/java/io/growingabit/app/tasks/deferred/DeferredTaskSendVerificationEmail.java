package io.growingabit.app.tasks.deferred;

import com.google.appengine.api.taskqueue.DeferredTask;

import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.gson.GsonFactory;

public class DeferredTaskSendVerificationEmail implements DeferredTask {

  private static final long serialVersionUID = 6444282313293774192L;
  private StudentEmailSignupStage studentEmailSignupStage;


  public DeferredTaskSendVerificationEmail(String jsonStudentEmailSignupStage) {
    this.studentEmailSignupStage = GsonFactory.getGsonInstance().fromJson(jsonStudentEmailSignupStage, StudentEmailSignupStage.class);
  }

  @Override
  public void run() {

    // TODO: send email with link MailGun
  }

}
