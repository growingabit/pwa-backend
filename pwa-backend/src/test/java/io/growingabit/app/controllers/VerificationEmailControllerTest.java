package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class VerificationEmailControllerTest extends BaseGaeTest {

  private String userId;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);

    SignupStageFactory.register(StudentEmailSignupStage.class);

    this.userId = "id";
  }

  @Test
  public void checkCode() {

    try {

      User user = new User();
      user.setId(this.userId);

      final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
      final SecurityContext context = Mockito.mock(SecurityContext.class);
      Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

      user = (User) new MeController().getCurrenUserInfo(context).getEntity();

      StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
      Response response = new MeController().studentemail(context, data);

      user = (User) response.getEntity();

      final String signupStageIndentifier = Settings.getConfig().getString(StudentEmailSignupStage.class.getCanonicalName());
      StudentEmailSignupStage stage = (StudentEmailSignupStage) user.getSignupStages().get(signupStageIndentifier).get();

      String verificationCode = Base64.encodeBase64URLSafeString(stage.getData().getVerificationCode().getBytes("utf-8"));

      response = new VerificationEmailController().verifyEmail(context, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void wrongVerificationCode() {
    try {

      User user = new User();
      user.setId(this.userId);

      final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
      final SecurityContext context = Mockito.mock(SecurityContext.class);
      Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

      user = (User) new MeController().getCurrenUserInfo(context).getEntity();

      StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
      Response response = new MeController().studentemail(context, data);

      user = (User) response.getEntity();

      final String signupStageIndentifier = Settings.getConfig().getString(StudentEmailSignupStage.class.getCanonicalName());
      user.getSignupStages().get(signupStageIndentifier).get();

      String verificationCode = Base64.encodeBase64URLSafeString("an invalid code".getBytes("utf-8"));

      response = new VerificationEmailController().verifyEmail(context, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void rigthCodeButNotEncoded() {

    try {

      User user = new User();
      user.setId(this.userId);

      final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
      final SecurityContext context = Mockito.mock(SecurityContext.class);
      Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

      user = (User) new MeController().getCurrenUserInfo(context).getEntity();

      StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
      Response response = new MeController().studentemail(context, data);

      user = (User) response.getEntity();

      final String signupStageIndentifier = Settings.getConfig().getString(StudentEmailSignupStage.class.getCanonicalName());
      StudentEmailSignupStage stage = (StudentEmailSignupStage) user.getSignupStages().get(signupStageIndentifier).get();

      String verificationCode = stage.getData().getVerificationCode();

      response = new VerificationEmailController().verifyEmail(context, verificationCode);

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void tsExpirationExpired() {

    try {

      User user = new User();
      user.setId(this.userId);

      final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
      final SecurityContext context = Mockito.mock(SecurityContext.class);
      Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

      user = (User) new MeController().getCurrenUserInfo(context).getEntity();

      StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
      Response response = new MeController().studentemail(context, data);

      user = (User) response.getEntity();

      final String signupStageIndentifier = Settings.getConfig().getString(StudentEmailSignupStage.class.getCanonicalName());
      StudentEmailSignupStage stage = (StudentEmailSignupStage) user.getSignupStages().get(signupStageIndentifier).get();

      String verificationCode = stage.getData().getVerificationCode();

      DateTimeUtils.setCurrentMillisFixed(new DateTime().plusDays(8).getMillis());

      response = new VerificationEmailController().verifyEmail(context, verificationCode);
      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

      DateTimeUtils.setCurrentMillisSystem();

    } catch (Exception e) {
      Assert.fail();
    }
  }


}
