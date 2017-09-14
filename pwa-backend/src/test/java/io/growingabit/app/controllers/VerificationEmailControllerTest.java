package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.common.utils.SignupStageFactory;
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

    userDao = new UserDao();

    userId = "id";
  }

  @Test
  @Ignore
  public void wrongCode() {

    final User user = new User();
    user.setId(userId);
    userDao.persist(user);
    final Auth0UserProfile userProfile = new Auth0UserProfile(user.getId(), "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    String verificationCode = "";
    Response response = new VerificationEmailController().verifyEmail(context, verificationCode);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);

    final User returnedUser = (User) response.getEntity();
    assertThat(returnedUser).isEqualTo(user);
  }

}
