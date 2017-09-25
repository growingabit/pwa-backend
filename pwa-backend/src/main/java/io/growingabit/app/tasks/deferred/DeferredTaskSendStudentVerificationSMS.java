package io.growingabit.app.tasks.deferred;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.googlecode.objectify.NotFoundException;

import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.sms.SMSSender;

public class DeferredTaskSendStudentVerificationSMS implements DeferredTask {

  private static final XLogger log = XLoggerFactory.getXLogger(DeferredTaskSendVerificationEmail.class);
  private static final Configuration config = Settings.getConfig();

  private final String studentPhoneSignupStageKey;

  public DeferredTaskSendStudentVerificationSMS(final String studentPhoneSignupStageKey) {
    this.studentPhoneSignupStageKey = studentPhoneSignupStageKey;
  }

  @Override
  public void run() {
    if (StringUtils.isNotEmpty(this.studentPhoneSignupStageKey)) {
      try {
        final String from = config.getString("twilio.from");
        final String text = config.getString("confirmation.phone.text");
        final StudentPhoneSignupStage stage = new StudentPhoneSignupStageDao().find(this.studentPhoneSignupStageKey);
        final StudentConfirmationPhone data = stage.getData();

        final String verificationLink = "https://" + stage.getData().getOrigin() + "/verify/phone/" + data.getVerificationCode();

        new SMSSender().sendMessage(from, data.getPhoneNumber(), text + " " + verificationLink);

      } catch (final NotFoundException e) {
        log.error("Signup stage not found " + this.studentPhoneSignupStageKey);
      } catch (final IllegalArgumentException e) {
        log.error("Signup key not valid " + this.studentPhoneSignupStageKey);
      } catch (final Throwable t) {
        // I don't want to throw exceptions
        log.error("Unknown exception ", t);
      }
    }
  }
}
