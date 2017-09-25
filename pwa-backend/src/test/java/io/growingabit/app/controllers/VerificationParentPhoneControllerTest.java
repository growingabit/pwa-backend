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
import org.mockito.Mockito;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.ParentPhoneVerificationTaskData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.app.utils.gson.GsonFactory;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.Utils;

public class VerificationParentPhoneControllerTest extends BaseGaeTest {

  private static final String HOST = "http://localhost";
  private static final String NAME = "name";
  private static final String SURNAME = "surname";
  private User currentUser;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(ParentPhoneSignupStage.class);
    ObjectifyService.register(StudentDataSignupStage.class);

    SignupStageFactory.register(ParentPhoneSignupStage.class);
    SignupStageFactory.register(StudentDataSignupStage.class);

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
  public void checkCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      Mockito.when(req.getHeader("Host")).thenReturn(HOST);
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

      response = new VerificationParentPhoneController().verifyPhone(verficationData);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

  @Test
  public void wrongVerificationCode() {
    try {
      final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
      Mockito.when(req.getHeader("Host")).thenReturn(HOST);
      final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, NAME, SURNAME);
      Response response = new MeController().parentphone(req, this.currentUser, data);

      response.getEntity();

      final String verificationCode = "an invalid code";
      final String userId = this.currentUser.getWebSafeKey();

      final ParentPhoneVerificationTaskData verificationTaskData = new ParentPhoneVerificationTaskData();
      verificationTaskData.setUserId(userId);
      verificationTaskData.setVerificationCode(verificationCode);

      final String verficationData = Base64.encodeBase64URLSafeString(GsonFactory.getGsonInstance().toJson(verificationTaskData).getBytes("utf-8"));

      response = new VerificationParentPhoneController().verifyPhone(verficationData);

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
      Mockito.when(req.getHeader("Host")).thenReturn(HOST);
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

      response = new VerificationParentPhoneController().verifyPhone(verficationData);

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationParentPhoneController().verifyPhone(verificationCode);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

      DateTimeUtils.setCurrentMillisSystem();

    } catch (final Exception e) {
      Assert.fail(ExceptionUtils.getStackTrace(e));
    }
  }

}
