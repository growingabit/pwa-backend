package io.growingabit.app.tasks.deferred;

import org.apache.commons.codec.binary.Base64;
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
import io.growingabit.app.model.ParentPhoneVerificationTaskData;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.gson.GsonFactory;
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

        final String verificationCode = parentPhoneData.getVerificationCode();
        final String userId = parentPhoneStage.getUser().toWebSafeString();

        final ParentPhoneVerificationTaskData verificationTaskData = new ParentPhoneVerificationTaskData();
        verificationTaskData.setUserId(userId);
        verificationTaskData.setVerificationCode(verificationCode);

        final String verficationData = Base64.encodeBase64URLSafeString(GsonFactory.getGsonInstance().toJson(verificationTaskData).getBytes("utf-8"));

        final String verificationLink = parentPhoneData.getOrigin() + "/verify/parentphone/" + verficationData;

        final ImmutableMap<String, String> map = ImmutableMap.of("verificationLink", verificationLink,
            "student.firstname", studentData.getName(),
            "student.lastname", studentData.getSurname(),
            "parent.firstname", parentPhoneData.getName(),
            "parent.lastname", parentPhoneData.getSurname()
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
