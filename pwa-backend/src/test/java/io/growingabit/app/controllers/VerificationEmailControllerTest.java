package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.mail.MailService;
import io.growingabit.testUtils.BaseDatastoreTest;
import io.growingabit.testUtils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class VerificationEmailControllerTest extends BaseDatastoreTest {

  private UserDao userDao;
  private String userId;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {

    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(StudentEmailSignupStage.class);

    SignupStageFactory.register(StudentEmailSignupStage.class);

    this.userDao = new UserDao();

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
      StudentEmailSignupStage stage = (StudentEmailSignupStage) user.getSignupStages().get(signupStageIndentifier).get();;

      Method method = MailService.class.getDeclaredMethod("createVerificationCode", StudentEmailSignupStage.class);
      method.setAccessible(true);
      String verificationCode = (String) method.invoke(null, stage);
      verificationCode = verificationCode.replace("/verificationemail/", "");

      response = new VerificationEmailController().verifyEmail(context, verificationCode);

      System.out.println((String) response.getEntity());

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void wronfVerificationCode() {

    try {

      User user = new User();
      user.setId(this.userId);

      final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
      final SecurityContext context = Mockito.mock(SecurityContext.class);
      Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

      user = (User) new MeController().getCurrenUserInfo(context).getEntity();

      StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
      new MeController().studentemail(context, data);

      Response response = new VerificationEmailController().verifyEmail(context, "an invalid code");

      assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    } catch (Exception e) {
      Assert.fail();
    }
  }


}
