package io.growingabit.app.signup.executors;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.app.dao.StudentBlockcertsSignupStageDao;
import io.growingabit.app.dao.StudentDataSignupStageDao;
import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.dao.WalletSetupSignupStageDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.BitcoinAddress;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.app.tasks.deferred.DeferredTaskSendParentVerificationSMS;
import io.growingabit.app.tasks.deferred.DeferredTaskSendStudentVerificationSMS;
import io.growingabit.app.tasks.deferred.DeferredTaskSendVerificationEmail;
import io.growingabit.app.utils.BitcoinAddressValidator;

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
      final StudentDataSignupStage userSignupStage = this.currentuser.getStage(StudentDataSignupStage.class);
      final StudentData data = new StudentData(stage.getData());
      userSignupStage.setData(data);
      userSignupStage.setDone();
      new StudentDataSignupStageDao().persist(userSignupStage);
    } catch (final IllegalArgumentException e) {
      throw new SignupStageExecutionException("Date is invalid", e);
    }
  }

  public void exec(final StudentEmailSignupStage stage) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);

    final StudentEmailSignupStage userSignupStage = this.currentuser.getStage(StudentEmailSignupStage.class);

    userSignupStage.setData(new StudentConfirmationEmail(stage.getData().getEmail(), stage.getData().getOrigin()));
    new StudentEmailSignupStageDao().persist(userSignupStage);

    final DeferredTaskSendVerificationEmail deferred = new DeferredTaskSendVerificationEmail(userSignupStage.getWebSafeKey());
    QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withPayload(deferred));
  }

  public void exec(final WalletSetupSignupStage stage) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);

    final BitcoinAddress address = stage.getData();
    if (BitcoinAddressValidator.isValid(address.getAddress())) {
      final WalletSetupSignupStage userSignupStage = this.currentuser.getStage(WalletSetupSignupStage.class);

      userSignupStage.setData(address);
      userSignupStage.setDone();
      new WalletSetupSignupStageDao().persist(userSignupStage);
    } else {
      throw new SignupStageExecutionException("Bitcoin address is invalid");
    }
  }

  public void exec(final StudentPhoneSignupStage stage) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);

    final StudentPhoneSignupStage userSignupStage = this.currentuser.getStage(StudentPhoneSignupStage.class);
    userSignupStage.setData(stage.getData());
    new StudentPhoneSignupStageDao().persist(userSignupStage);

    final DeferredTaskSendStudentVerificationSMS deferred = new DeferredTaskSendStudentVerificationSMS(userSignupStage.getWebSafeKey());
    QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withPayload(deferred));
  }

  public void exec(final ParentPhoneSignupStage stage) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);

    final ParentPhoneSignupStage userSignupStage = this.currentuser.getStage(ParentPhoneSignupStage.class);
    userSignupStage.setData(stage.getData());
    new ParentPhoneSignupStageDao().persist(userSignupStage);

    final StudentDataSignupStage dataSignupStage = this.currentuser.getStage(StudentDataSignupStage.class);

    final DeferredTaskSendParentVerificationSMS deferred = new DeferredTaskSendParentVerificationSMS(userSignupStage.getWebSafeKey(), dataSignupStage.getWebSafeKey());
    QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withPayload(deferred));
  }

  public void exec(final StudentBlockcertsSignupStage stage) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);

    final StudentBlockcertsSignupStage userSignupStage = this.currentuser.getStage(StudentBlockcertsSignupStage.class);
    userSignupStage.setData(stage.getData());

    new StudentBlockcertsSignupStageDao().persist(userSignupStage);
  }
}
