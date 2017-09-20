package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.BitcoinAddress;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.DummySignupStage;
import io.growingabit.testUtils.Utils;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeControllerTest extends BaseGaeTest {

  private InvitationDao invitationDao;
  private User currentUser;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
    Utils.clearSignupStageFactory();

    ObjectifyService.register(User.class);
    ObjectifyService.register(DummySignupStage.class);
    ObjectifyService.register(Invitation.class);
    ObjectifyService.register(InvitationCodeSignupStage.class);
    ObjectifyService.register(StudentDataSignupStage.class);
    ObjectifyService.register(StudentEmailSignupStage.class);
    ObjectifyService.register(StudentPhoneSignupStage.class);
    ObjectifyService.register(WalletSetupSignupStage.class);

    SignupStageFactory.registerMandatory(DummySignupStage.class);
    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);
    SignupStageFactory.register(WalletSetupSignupStage.class);

    this.invitationDao = new InvitationDao();
    final String userId = "id";

    final Auth0UserProfile userProfile = new Auth0UserProfile(userId, "name");
    final SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getUserPrincipal()).thenReturn(userProfile);

    final ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
    Mockito.when(requestContext.getSecurityContext()).thenReturn(context);

    //to create the user
    new UserCreationFilter().filter(requestContext);
    this.currentUser = new UserDao().find(Key.create(User.class, userId));
  }

  @Test
  public void returnCurrentUser() {
    final Response response = new MeController().getCurrenUserInfo(this.currentUser);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    assertThat(returnedUser).isEqualTo(this.currentUser);
  }

  @Test
  public void confirmInvitationCode() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final Invitation i = Mockito.mock(Invitation.class);
    Mockito.when(i.getInvitationCode()).thenReturn(invitation.getInvitationCode());

    final Response response = new MeController().confirmInvitationCode(this.currentUser, i);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();

    final InvitationCodeSignupStage savedStage = returnedUser.getStage(InvitationCodeSignupStage.class);

    assertThat(savedStage.getData().isConfirmed()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
  }

  @Test
  public void invitationCodeAlreadyUsed() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    invitation.setConfirmed();
    this.invitationDao.persist(invitation);

    final Invitation i = Mockito.mock(Invitation.class);
    Mockito.when(i.getInvitationCode()).thenReturn(invitation.getInvitationCode());

    final Response response = new MeController().confirmInvitationCode(this.currentUser, i);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void invitationCodeNotFound() {
    final Invitation i = Mockito.mock(Invitation.class);
    Mockito.when(i.getInvitationCode()).thenReturn("inexintent code");

    final Response response = new MeController().confirmInvitationCode(this.currentUser, i);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentDataStep() {
    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    final Response response = new MeController().studentData(this.currentUser, studentData);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();

    final StudentDataSignupStage savedStage = returnedUser.getStage(StudentDataSignupStage.class);

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData().getName()).isEqualTo(studentData.getName());
    assertThat(savedStage.getData().getSurname()).isEqualTo(studentData.getSurname());
    assertThat(savedStage.getData().getBirthdate()).isEqualTo(studentData.getBirthdate());

  }

  @Test
  public void studentDataNull() {
    final Response response = new MeController().studentData(this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void badRequestIfBirthdateIsInvalid() {
    Response response = new MeController().getCurrenUserInfo(this.currentUser);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final StudentData studentData = Mockito.mock(StudentData.class);
    Mockito.when(studentData.getBirthdate()).thenReturn(null);

    response = new MeController().studentData(this.currentUser, studentData);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentEmailDataNull() {
    final Response response = new MeController().studentemail(this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentEmailDataHasEmailFieldEmpty() {
    final StudentConfirmationEmail data = Mockito.mock(StudentConfirmationEmail.class);
    Mockito.when(data.getEmail()).thenReturn("");

    final Response response = new MeController().studentemail(this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentEmailDataHasEmailFieldNull() {
    final StudentConfirmationEmail data = Mockito.mock(StudentConfirmationEmail.class);
    Mockito.when(data.getEmail()).thenReturn(null);

    final Response response = new MeController().studentemail(this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentEmailStage() {
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com");
    final Response response = new MeController().studentemail(this.currentUser, data);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final StudentEmailSignupStage savedStage = returnedUser.getStage(StudentEmailSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getEmail()).isEqualTo(data.getEmail());
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().plusDays(6).getMillis());
    assertThat(savedStage.getData().getTsExpiration()).isLessThan(new DateTime().plusDays(8).getMillis());
  }


  @Test
  public void studentPhoneDataNull() {
    final Response response = new MeController().studentphone(this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentPhoneDataHasPhoneFieldEmpty() {
    final StudentConfirmationPhone data = Mockito.mock(StudentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("");

    final Response response = new MeController().studentphone(this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentPhoneDataHasPhoneFieldNull() {
    final StudentConfirmationPhone data = Mockito.mock(StudentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn(null);

    final Response response = new MeController().studentphone(this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentPhoneStage() {
    final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");
    final Response response = new MeController().studentphone(this.currentUser, data);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final StudentPhoneSignupStage savedStage = returnedUser.getStage(StudentPhoneSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getPhoneNumber()).isEqualTo(data.getPhoneNumber());
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().plusDays(6).getMillis());
    assertThat(savedStage.getData().getTsExpiration()).isLessThan(new DateTime().plusDays(8).getMillis());
  }


  @Test
  public void completeWalletDataSignupStage() {
    final String validAddress = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i";
    final BitcoinAddress address = new BitcoinAddress(validAddress);

    final Response response = new MeController().walletSetup(this.currentUser, address);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();

    final WalletSetupSignupStage savedStage = returnedUser.getStage(WalletSetupSignupStage.class);

    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.isDone()).isTrue();
    assertThat(savedStage.getData().getAddress()).isEqualTo(address.getAddress());
  }

  @Test
  public void doNotcompleteWalletDataSignupStageIfAddressIsInvalid() {
    final String invalidAddress = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j";
    final BitcoinAddress address = new BitcoinAddress(invalidAddress);

    final Response response = new MeController().walletSetup(this.currentUser, address);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void walletAddressMustBeNotNull() {
    final BitcoinAddress address = Mockito.mock(BitcoinAddress.class);
    Mockito.when(address.getAddress()).thenReturn("");

    final Response response = new MeController().walletSetup(this.currentUser, address);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void walletSetupSignupStageMustBeNotNull() {
    final Response response = new MeController().walletSetup(this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

}
