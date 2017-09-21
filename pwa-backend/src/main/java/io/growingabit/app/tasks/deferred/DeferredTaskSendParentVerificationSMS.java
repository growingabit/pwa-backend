package io.growingabit.app.tasks.deferred;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StrSubstitutor;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.common.collect.ImmutableMap;
import com.googlecode.objectify.NotFoundException;

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.app.dao.StudentDataSignupStageDao;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.sms.SMSSender;

public class DeferredTaskSendParentVerificationSMS implements DeferredTask {

  private static final XLogger log = XLoggerFactory.getXLogger(DeferredTaskSendVerificationEmail.class);
  private static final Configuration config = Settings.getConfig();

  private final String parentPhoneSignupStageKey;
  private final String studentDataSignupStageKey;

  public DeferredTaskSendParentVerificationSMS(final String parentPhoneSignupStageKey, final String studentDataSignupStageKey) {
    this.parentPhoneSignupStageKey = parentPhoneSignupStageKey;
    this.studentDataSignupStageKey = studentDataSignupStageKey;
  }

  @Override
  public void run() {
    if (StringUtils.isNotEmpty(this.parentPhoneSignupStageKey)) {
      try {
        final String from = config.getString("twilio.from");
        final String text = config.getString("parent.confirmation.phone.text");

        final ParentPhoneSignupStage parentPhoneStage = new ParentPhoneSignupStageDao().find(this.parentPhoneSignupStageKey);
        final StudentDataSignupStage studendDataStage = new StudentDataSignupStageDao().find(this.studentDataSignupStageKey);

        final ParentConfirmationPhone parentPhoneData = parentPhoneStage.getData();
        final StudentData studentData = studendDataStage.getData();

        final String verificationLink = "https://" + parentPhoneData.getOriginHost() + "/verify/phone/" + parentPhoneData.getVerificationCode();

        final ImmutableMap<String, String> map = ImmutableMap.of("verificationLink", verificationLink,
            "student.firstname", parentPhoneData.getName(),
            "student.lastname", parentPhoneData.getSurname(),
            "parent.firstname", studentData.getName(),
            "parent.lastname", studentData.getSurname()
        );

        new SMSSender().sendMessage(from, parentPhoneData.getPhoneNumber(), new StrSubstitutor(map).replace(text));

      } catch (final NotFoundException e) {
        log.error("Signup stage not found " + this.parentPhoneSignupStageKey);
      } catch (final IllegalArgumentException e) {
        log.error("Signup key not valid " + this.parentPhoneSignupStageKey);
      } catch (final Throwable t) {
        // I don't want to throw exceptions
        log.error("Unknown exception ", t);
      }
    }
  }
}
