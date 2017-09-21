package io.growingabit.app.tasks.deferred;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.appengine.api.taskqueue.DeferredTask;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.mail.MailService;

public class DeferredTaskSendVerificationEmail implements DeferredTask {

  private static final long serialVersionUID = 6444282313293774192L;
  private static final XLogger log = XLoggerFactory.getXLogger(DeferredTaskSendVerificationEmail.class);

  private final String studentEmailSignupStageWebsafeString;

  public DeferredTaskSendVerificationEmail(final String studentEmailSignupStageWebsafeString) {
    this.studentEmailSignupStageWebsafeString = studentEmailSignupStageWebsafeString;
  }

  @Override
  public void run() {

    try {
      final StudentEmailSignupStage stage = new StudentEmailSignupStageDao().find(this.studentEmailSignupStageWebsafeString);
      MailService.sendVerificationEmail(stage);
    } catch (final Throwable e) {
      log.error("During execute task for " + this.studentEmailSignupStageWebsafeString, e);
    }
  }

}
