package io.growingabit.app.signup.executors;

import static com.google.common.truth.Truth.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.common.base.Splitter;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.BitcoinAddress;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationBlockcerts;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.Utils;

public class SignupStageExecutorTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";

  private UserDao userDao;
  private InvitationDao invitationDao;
  private User user;

  @Before
  public void setup() throws InstantiationException, IllegalAccessException, NoSuchFieldException {

    Utils.clearSignupStageFactory();

    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(User.class);

    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    ObjectifyService.factory().register(StudentDataSignupStage.class);
    ObjectifyService.factory().register(StudentEmailSignupStage.class);
    ObjectifyService.factory().register(StudentPhoneSignupStage.class);
    ObjectifyService.factory().register(ParentPhoneSignupStage.class);
    ObjectifyService.factory().register(WalletSetupSignupStage.class);
    ObjectifyService.factory().register(StudentBlockcertsSignupStage.class);

    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);
    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);
    SignupStageFactory.register(ParentPhoneSignupStage.class);
    SignupStageFactory.register(WalletSetupSignupStage.class);
    SignupStageFactory.register(StudentBlockcertsSignupStage.class);

    this.userDao = new UserDao();
    this.invitationDao = new InvitationDao();

    this.user = new User();
    this.user.setId("id");
    this.userDao.persist(this.user);
    final Key<User> userKey = Key.create(this.user);
    for (final SignupStage signupStage : SignupStageFactory.getMandatorySignupStages(userKey)) {
      this.user.addMandatorySignupStage(signupStage);
    }
    for (final SignupStage signupStage : SignupStageFactory.getSignupStages(userKey)) {
      this.user.addSignupStage(signupStage);
    }
    this.userDao.persist(this.user);
  }

  @Test(expected = NullPointerException.class)
  public void invitationStepUserMustBeNotNull() {
    new SignupStageExecutor(null);
  }

  @Test
  public void completeInvitationStep() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setData(invitation);
    new SignupStageExecutor(this.user).exec(stage);

    final InvitationCodeSignupStage savedStage = (InvitationCodeSignupStage) this.user.getMandatorySignupStages().values().iterator().next().get();

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test(expected = SignupStageExecutionException.class)
  public void notCompleteInvitationStepIfCodeIsUsed() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    invitation.setConfirmed();
    this.invitationDao.persist(invitation);

    final InvitationCodeSignupStage stage = new InvitationCodeSignupStage();
    stage.setData(invitation);
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test(expected = NullPointerException.class)
  public void invitationStepStageMustBeNotNull() {
    final InvitationCodeSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test
  public void completeStudentDataStep() {
    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    final StudentDataSignupStage stage = new StudentDataSignupStage();
    stage.setData(studentData);
    new SignupStageExecutor(this.user).exec(stage);

    final StudentDataSignupStage savedStage = this.user.getStage(StudentDataSignupStage.class);

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData().getName()).isEqualTo(studentData.getName());
    assertThat(savedStage.getData().getSurname()).isEqualTo(studentData.getSurname());
    assertThat(savedStage.getData().getBirthdate()).isEqualTo(studentData.getBirthdate());
  }

  @Test(expected = NullPointerException.class)
  public void userDataStepStageMustBeNotNull() {
    final StudentDataSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test
  public void completeStudentEmailConfirmationStep() throws InterruptedException {

    final StudentConfirmationEmail data = new StudentConfirmationEmail("test@example.com", "http://localhost");

    final StudentEmailSignupStage stage = new StudentEmailSignupStage();
    stage.setData(data);
    new SignupStageExecutor(this.user).exec(stage);

    final StudentEmailSignupStage savedStage = this.user.getStage(StudentEmailSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getEmail()).isEqualTo(data.getEmail());
    assertThat(savedStage.getData().getTsExpiration()).isNotNull();
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().getMillis());
    assertThat(savedStage.getData().getVerificationCode()).isNotNull();
    assertThat(savedStage.getData().getVerificationCode()).isNotEmpty();

    // TODO: try to avoid this magic number here
    Thread.sleep(1000);
    final LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    final QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
    Assert.assertEquals(1, qsi.getTaskInfo().size());
  }

  @Test(expected = NullPointerException.class)
  public void userEmailStepStageMustBeNotNull() {
    final StudentEmailSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

  public void completeWalletDataSignupStage() {
    final String validAddress = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i";
    final BitcoinAddress address = new BitcoinAddress(validAddress);
    final WalletSetupSignupStage stage = new WalletSetupSignupStage();
    stage.setData(address);

    new SignupStageExecutor(this.user).exec(stage);

    final WalletSetupSignupStage savedStage = this.user.getStage(WalletSetupSignupStage.class);

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData().getAddress()).isEqualTo(address.getAddress());
  }

  @Test(expected = SignupStageExecutionException.class)
  public void doNotcompleteWalletDataSignupStageIfAddressIsInvalid() {
    final String invalidAddress = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j";
    final BitcoinAddress address = new BitcoinAddress(invalidAddress);
    final WalletSetupSignupStage stage = new WalletSetupSignupStage();
    stage.setData(address);

    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test(expected = NullPointerException.class)
  public void walletSetupSignupStageMustBeNotNull() {
    final WalletSetupSignupStage stage = null;
    new SignupStageExecutor(this.user).exec(stage);
  }

  @Test
  public void completeStudentPhoneConfirmationStep() throws InterruptedException {

    final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");

    final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
    stage.setData(data);
    new SignupStageExecutor(this.user).exec(stage);

    final StudentPhoneSignupStage savedStage = this.user.getStage(StudentPhoneSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getPhoneNumber()).isEqualTo(data.getPhoneNumber());
    assertThat(savedStage.getData().getTsExpiration()).isNotNull();
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().getMillis());
    assertThat(savedStage.getData().getVerificationCode()).isNotNull();
    assertThat(savedStage.getData().getVerificationCode()).isNotEmpty();

    // TODO: try to avoid this magic number here
    Thread.sleep(1000);
    final LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    final QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
    Assert.assertEquals(1, qsi.getTaskInfo().size());
  }

  @Test
  public void completeStudentBlockcertsStep() throws InterruptedException {

    try {

      final StudentConfirmationBlockcerts data = new StudentConfirmationBlockcerts("http://locahost", this.user.getId());

      final StudentBlockcertsSignupStage stage = new StudentBlockcertsSignupStage();
      stage.setData(data);
      new SignupStageExecutor(this.user).exec(stage);

      final StudentBlockcertsSignupStage savedStage = this.user.getStage(StudentBlockcertsSignupStage.class);

      assertThat(savedStage.isDone()).isFalse();
      assertThat(savedStage.getData().getOrigin()).isNotNull();
      assertThat(savedStage.getData().getUserId()).isNotNull();
      assertThat(savedStage.getData().getTsExpiration()).isNotNull();
      assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().getMillis());


      List<String> list = Splitter.on(":").splitToList(new String(Base64.decodeBase64(savedStage.getData().getNonce()), "utf-8"));
      String userId = list.get(0);
      String hash = list.get(1);

      assertThat(hash).isEqualTo(StringUtils.left(new String(DigestUtils.sha1(data.getUserId() + data.getTsExpiration() + data.getOrigin()), "utf-8"), 5));
      assertThat(userId).isEqualTo(data.getUserId());

    } catch (UnsupportedEncodingException e) {
      Assert.fail();
    }
  }

  @Test
  public void completeParentPhoneConfirmationStep() throws InterruptedException {

    final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, "name", "surname");

    final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
    stage.setData(data);
    new SignupStageExecutor(this.user).exec(stage);

    final ParentPhoneSignupStage savedStage = this.user.getStage(ParentPhoneSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getPhoneNumber()).isEqualTo(data.getPhoneNumber());
    assertThat(savedStage.getData().getName()).isEqualTo(data.getName());
    assertThat(savedStage.getData().getSurname()).isEqualTo(data.getSurname());
    assertThat(savedStage.getData().getTsExpiration()).isNotNull();
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().getMillis());
    assertThat(savedStage.getData().getVerificationCode()).isNotNull();
    assertThat(savedStage.getData().getVerificationCode()).isNotEmpty();

    // TODO: try to avoid this magic number here
    Thread.sleep(1000);
    final LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
    final QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
    Assert.assertEquals(1, qsi.getTaskInfo().size());
  }

}
