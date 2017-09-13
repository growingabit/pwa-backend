package io.growingabit.app.tasks.deferred;

import java.io.UnsupportedEncodingException;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.appengine.api.taskqueue.DeferredTask;

import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.utils.gson.GsonFactory;
import io.growingabit.mail.MailService;

public class DeferredTaskSendVerificationEmail implements DeferredTask {

  private static final long serialVersionUID = 6444282313293774192L;
  private static final XLogger log = XLoggerFactory.getXLogger(DeferredTaskSendVerificationEmail.class);

  private String studentEmailSignupStageJson;

  public DeferredTaskSendVerificationEmail(String studentEmailSignupStageJson) {
    this.studentEmailSignupStageJson = studentEmailSignupStageJson;
  }

  @Override
  public void run() {

    try {
      StudentEmailSignupStage stage = GsonFactory.getGsonInstance().fromJson(this.studentEmailSignupStageJson, StudentEmailSignupStage.class);
      MailService.sendVerificationEmail(stage);
    } catch (UnsupportedEncodingException e) {
      log.catching(e);
    }
  }

}
