package io.growingabit.app.tasks.deferred;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.googlecode.objectify.NotFoundException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.utils.Settings;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class DeferredTaskSendVerificationSMS implements DeferredTask {

  private static final XLogger log = XLoggerFactory.getXLogger(DeferredTaskSendVerificationEmail.class);
  private static final Configuration config = Settings.getConfig();

  private final String studentPhoneSignupStageKey;

  public DeferredTaskSendVerificationSMS(final String studentPhoneSignupStageKey) {
    this.studentPhoneSignupStageKey = studentPhoneSignupStageKey;
  }

  @Override
  public void run() {
    if (StringUtils.isNotEmpty(this.studentPhoneSignupStageKey)) {
      try {
        final String accountSid = config.getString("twilio.accountSid");
        final String authToken = config.getString("twilio.accountToken");
        final String from = config.getString("twilio.from");
        final String text = config.getString("confirmation.phone.text");
        final StudentPhoneSignupStage stage = new StudentPhoneSignupStageDao().find(this.studentPhoneSignupStageKey);
        final StudentConfirmationPhone data = stage.getData();

        Twilio.init(accountSid, authToken);

        final Message message = Message.creator(
            new PhoneNumber(data.getPhoneNumber()),  // to
            new PhoneNumber(from),                   // from
            text + " " + data.getVerificationCode())
            .create();
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
