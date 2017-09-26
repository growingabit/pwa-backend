package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.RequestUtils;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class VerificationControllerTest extends BaseGaeTest {

  private static final String HOST = "http://localhost";
  private User currentUser;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);
    ObjectifyService.register(StudentPhoneSignupStage.class);

    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);

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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);
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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);
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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);
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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);
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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006", HOST);
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
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006", HOST);
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
  @Ignore
  public void phoneTsExpirationExpired() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      Mockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
      final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006", HOST);
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

}
