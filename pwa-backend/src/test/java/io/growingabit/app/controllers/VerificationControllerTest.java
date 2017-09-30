package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.base.Joiner;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.ParentPhoneVerificationTaskData;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationBlockcerts;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.RequestUtils;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.app.utils.gson.GsonFactory;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.Utils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestUtils.class)
public class VerificationControllerTest extends BaseGaeTest {

  private static final String EMAIL_EXAMPLE_COM = "email@example.com";
  private static final String HOST = "http://localhost";
  private static final String NAME = "name";
  private static final String SURNAME = "surname";
  private User currentUser;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);
    ObjectifyService.register(StudentDataSignupStage.class);
    ObjectifyService.register(StudentPhoneSignupStage.class);
    ObjectifyService.register(ParentPhoneSignupStage.class);
    ObjectifyService.register(StudentBlockcertsSignupStage.class);

    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);
    SignupStageFactory.register(ParentPhoneSignupStage.class);
    SignupStageFactory.register(StudentBlockcertsSignupStage.class);

    final String userId = "id";

    final Auth0UserProfile userProfile = new Auth0UserProfile(userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    // to create the user
    new UserCreationFilter().filter(requestContext);
    this.currentUser = new UserDao().find(Key.create(User.class, userId));
  }

  @Test
  public void checkEmailCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail(EMAIL_EXAMPLE_COM, HOST);
      Response response = new MeController().studentemail(req, this.currentUser, data);
      final User user = (User) response.getEntity();

      final StudentEmailSignupStage stage = user.getStage(StudentEmailSignupStage.class);

      final String verificationCode = Base64.encodeBase64URLSafeString(stage.getData().getVerificationCode().getBytes("utf-8"));

      response = new VerificationController().verifyEmail(this.currentUser, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void wrongEmailVerificationCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail(EMAIL_EXAMPLE_COM, HOST);
      Response response = new MeController().studentemail(req, this.currentUser, data);

      response.getEntity();

      final String verificationCode = Base64.encodeBase64URLSafeString("an invalid code".getBytes("utf-8"));

      response = new VerificationController().verifyEmail(this.currentUser, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void rigthCodeButNotEncoded() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail(EMAIL_EXAMPLE_COM, HOST);
      Response response = new MeController().studentemail(req, this.currentUser, data);

      final User user = (User) response.getEntity();

      final StudentEmailSignupStage stage = user.getStage(StudentEmailSignupStage.class);

      final String verificationCode = stage.getData().getVerificationCode();

      response = new VerificationController().verifyEmail(this.currentUser, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  @Ignore
  public void emailTsExpirationExpired() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      Mockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail(EMAIL_EXAMPLE_COM, HOST);
      Response response = new MeController().studentemail(req, this.currentUser, data);

      final User user = (User) response.getEntity();

      final StudentEmailSignupStage stage = user.getStage(StudentEmailSignupStage.class);

      final String verificationCode = Base64.encodeBase64URLSafeString(stage.getData().getVerificationCode().getBytes("utf-8"));

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationController().verifyEmail(this.currentUser, verificationCode);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

      DateTimeUtils.setCurrentMillisSystem();

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void checkPhoneCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");
      Response response = new MeController().studentphone(req, this.currentUser, data);
      final User user = (User) response.getEntity();

      final StudentPhoneSignupStage stage = user.getStage(StudentPhoneSignupStage.class);

      final String verificationCode = stage.getData().getVerificationCode();

      response = new VerificationController().verifyPhone(this.currentUser, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void wrongPhoneVerificationCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      Mockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");
      Response response = new MeController().studentphone(req, this.currentUser, data);

      response.getEntity();

      final String verificationCode = "an invalid code";

      response = new VerificationController().verifyPhone(this.currentUser, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void phoneTsExpirationExpired() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");
      Response response = new MeController().studentphone(req, this.currentUser, data);

      final User user = (User) response.getEntity();

      final StudentPhoneSignupStage stage = user.getStage(StudentPhoneSignupStage.class);

      final String verificationCode = stage.getData().getVerificationCode();

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationController().verifyPhone(this.currentUser, verificationCode);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

      DateTimeUtils.setCurrentMillisSystem();

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void checkCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, NAME, SURNAME);
      Response response = new MeController().parentphone(req, this.currentUser, data);
      final User user = (User) response.getEntity();

      final ParentPhoneSignupStage stage = user.getStage(ParentPhoneSignupStage.class);

      final String verificationCode = stage.getData().getVerificationCode();
      final String userId = this.currentUser.getWebSafeKey();

      final ParentPhoneVerificationTaskData verificationTaskData = new ParentPhoneVerificationTaskData();
      verificationTaskData.setUserId(userId);
      verificationTaskData.setVerificationCode(verificationCode);

      final String verficationData = Base64.encodeBase64URLSafeString(GsonFactory.getGsonInstance().toJson(verificationTaskData).getBytes("utf-8"));

      response = new VerificationController().verifyPhone(verficationData);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void wrongVerificationCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, NAME, SURNAME);
      Response response = new MeController().parentphone(req, this.currentUser, data);

      response.getEntity();

      final String verificationCode = "an invalid code";
      final String userId = this.currentUser.getWebSafeKey();

      final ParentPhoneVerificationTaskData verificationTaskData = new ParentPhoneVerificationTaskData();
      verificationTaskData.setUserId(userId);
      verificationTaskData.setVerificationCode(verificationCode);

      final String verficationData = Base64.encodeBase64URLSafeString(GsonFactory.getGsonInstance().toJson(verificationTaskData).getBytes("utf-8"));

      response = new VerificationController().verifyPhone(verficationData);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  @Ignore
  public void tsExpirationExpired() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      Mockito.when(RequestUtils.getHost(req)).thenReturn(HOST);
      final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, NAME, SURNAME);
      Response response = new MeController().parentphone(req, this.currentUser, data);

      final User user = (User) response.getEntity();

      final ParentPhoneSignupStage stage = user.getStage(ParentPhoneSignupStage.class);

      final String verificationCode = stage.getData().getVerificationCode();
      final String userId = this.currentUser.getWebSafeKey();

      final ParentPhoneVerificationTaskData verificationTaskData = new ParentPhoneVerificationTaskData();
      verificationTaskData.setUserId(userId);
      verificationTaskData.setVerificationCode(verificationCode);

      final String verficationData = Base64.encodeBase64URLSafeString(GsonFactory.getGsonInstance().toJson(verificationTaskData).getBytes("utf-8"));

      response = new VerificationController().verifyPhone(verficationData);

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationController().verifyPhone(verificationCode);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

      DateTimeUtils.setCurrentMillisSystem();

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void checkBlockcertsVerify() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      Response response = new MeController().blockcerts(req, this.currentUser);

      final User user = (User) response.getEntity();
      final StudentBlockcertsSignupStage stage = user.getStage(StudentBlockcertsSignupStage.class);

      final StudentConfirmationBlockcerts s = GsonFactory.getGsonInstance().fromJson(GsonFactory.getGsonInstance().toJson(stage.getData()), StudentConfirmationBlockcerts.class);
      s.setBitcoinAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i");

      response = new VerificationController().verifyBlockcerts(s);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void invalidBitcoinAddress() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      Response response = new MeController().blockcerts(req, this.currentUser);

      final User user = (User) response.getEntity();
      final StudentBlockcertsSignupStage stage = user.getStage(StudentBlockcertsSignupStage.class);

      final StudentConfirmationBlockcerts s = GsonFactory.getGsonInstance().fromJson(GsonFactory.getGsonInstance().toJson(stage.getData()), StudentConfirmationBlockcerts.class);
      s.setBitcoinAddress("fofoofoofofofoof");

      response = new VerificationController().verifyBlockcerts(s);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void invalidBase64Address() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      Response response = new MeController().blockcerts(req, this.currentUser);

      final User user = (User) response.getEntity();
      user.getStage(StudentBlockcertsSignupStage.class);

      final StudentConfirmationBlockcerts s = GsonFactory.getGsonInstance().fromJson("{ \"bitcoinAddress\" : \"1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i\",\"nonce\" : \"invalid nonce\"}", StudentConfirmationBlockcerts.class);

      response = new VerificationController().verifyBlockcerts(s);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void invalidNonceAddress() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      Response response = new MeController().blockcerts(req, this.currentUser);

      final User user = (User) response.getEntity();
      user.getStage(StudentBlockcertsSignupStage.class);

      final String hash = StringUtils.left(new String(DigestUtils.sha1("invalid nonce"), "utf-8"), 5);
      final String nonce = Base64.encodeBase64URLSafeString(Joiner.on(":").join(this.currentUser.getId(), hash).getBytes("utf-8"));
      ;
      final StudentConfirmationBlockcerts s = GsonFactory.getGsonInstance().fromJson("{ \"bitcoinAddress\" : \"1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i\",\"nonce\" : " + nonce + "}", StudentConfirmationBlockcerts.class);

      response = new VerificationController().verifyBlockcerts(s);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void blockcertsTsExpired() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      PowerMockito.mockStatic(RequestUtils.class);
      PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      Response response = new MeController().blockcerts(req, this.currentUser);

      final User user = (User) response.getEntity();
      final StudentBlockcertsSignupStage stage = user.getStage(StudentBlockcertsSignupStage.class);

      final StudentConfirmationBlockcerts s = GsonFactory.getGsonInstance().fromJson(GsonFactory.getGsonInstance().toJson(stage.getData()), StudentConfirmationBlockcerts.class);
      s.setBitcoinAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i");

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationController().verifyBlockcerts(s);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

}
