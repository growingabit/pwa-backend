package io.growingabit.app.controllers;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.BitcoinAddress;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.app.utils.RequestUtils;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.utils.SignupStageFactory;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.testUtils.BaseGaeTest;
import io.growingabit.testUtils.DummySignupStage;
import io.growingabit.testUtils.Utils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestUtils.class)
public class MeControllerTest extends BaseGaeTest {

  private static final String HOST = "http://www.example.com";

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
    ObjectifyService.register(ParentPhoneSignupStage.class);
    ObjectifyService.register(StudentBlockcertsSignupStage.class);

    SignupStageFactory.registerMandatory(DummySignupStage.class);
    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    SignupStageFactory.register(DummySignupStage.class);
    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);
    SignupStageFactory.register(ParentPhoneSignupStage.class);
    SignupStageFactory.register(WalletSetupSignupStage.class);
    SignupStageFactory.register(StudentBlockcertsSignupStage.class);

    this.invitationDao = new InvitationDao();
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
  public void doubleInvitationCode() {
    final Invitation invitation = new Invitation("My school1", "My class1", "This Year1", "My Spec1");
    this.invitationDao.persist(invitation);

    final Invitation i = Mockito.mock(Invitation.class);
    Mockito.when(i.getInvitationCode()).thenReturn(invitation.getInvitationCode());

    new MeController().confirmInvitationCode(this.currentUser, i);
    final Response response = new MeController().confirmInvitationCode(this.currentUser, i);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);

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
  public void doubleStudentDataStep() {
    final StudentData studentData = new StudentData();
    studentData.setName("Lorenzo");
    studentData.setSurname("Bugiani");
    studentData.setBirthdate("19/04/1985");

    new MeController().studentData(this.currentUser, studentData);
    final Response response = new MeController().studentData(this.currentUser, studentData);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
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
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    final Response response = new MeController().studentemail(req, this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentEmailDataHasEmailFieldEmpty() {
    final StudentConfirmationEmail data = Mockito.mock(StudentConfirmationEmail.class);
    Mockito.when(data.getEmail()).thenReturn("");
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().studentemail(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentEmailDataHasEmailFieldNull() {
    final StudentConfirmationEmail data = Mockito.mock(StudentConfirmationEmail.class);
    Mockito.when(data.getEmail()).thenReturn(null);
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().studentemail(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentEmailStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);

    final Response response = new MeController().studentemail(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final StudentEmailSignupStage savedStage = returnedUser.getStage(StudentEmailSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getEmail()).isEqualTo(data.getEmail());
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().plusDays(6).getMillis());
    assertThat(savedStage.getData().getTsExpiration()).isLessThan(new DateTime().plusDays(8).getMillis());
  }

  @Test
  public void doubleStudentEmailStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
    final StudentConfirmationEmail data = new StudentConfirmationEmail("email@example.com", HOST);

    new MeController().studentemail(req, this.currentUser, data);

    this.currentUser.getStage(StudentEmailSignupStage.class).setDone();

    final Response response = new MeController().studentemail(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
  }

  @Test
  public void studentPhoneDataNull() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    final Response response = new MeController().studentphone(req, this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentPhoneDataHasPhoneFieldEmpty() {
    final StudentConfirmationPhone data = Mockito.mock(StudentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("");

    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().studentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void studentPhoneDataHasPhoneFieldNull() {
    final StudentConfirmationPhone data = Mockito.mock(StudentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn(null);
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().studentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeStudentPhoneStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);

    final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");

    final Response response = new MeController().studentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final StudentPhoneSignupStage savedStage = returnedUser.getStage(StudentPhoneSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getPhoneNumber()).isEqualTo(data.getPhoneNumber());
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().plusDays(6).getMillis());
    assertThat(savedStage.getData().getTsExpiration()).isLessThan(new DateTime().plusDays(8).getMillis());
  }

  @Test
  public void doubleStudentPhoneStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);

    final StudentConfirmationPhone data = new StudentConfirmationPhone("+15005550006");

    new MeController().studentphone(req, this.currentUser, data);

    this.currentUser.getStage(StudentPhoneSignupStage.class).setDone();

    final Response response = new MeController().studentphone(req, this.currentUser, data);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
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
  public void doubleWalletDataSignupStage() {
    final String validAddress = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i";
    final BitcoinAddress address = new BitcoinAddress(validAddress);

    new MeController().walletSetup(this.currentUser, address);

    final Response response = new MeController().walletSetup(this.currentUser, address);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
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

  @Test(expected = IllegalArgumentException.class)
  public void blockcertsOriginAndHostNull() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    Mockito.when(req.getHeader("Origin")).thenReturn(null);
    Mockito.when(req.getHeader("Host")).thenReturn(null);
    new MeController().blockcerts(req, this.currentUser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void blockcertsUserIdNull() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    this.currentUser.setId(null);
    new MeController().blockcerts(req, this.currentUser);
  }

  @Test
  public void completeBlockcertsStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);
    final Response response = new MeController().blockcerts(req, this.currentUser);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
  }

  @Test
  public void doubleBlockcertsStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getOrigin(req)).thenReturn(HOST);

    new MeController().blockcerts(req, this.currentUser);
    this.currentUser.getStage(StudentBlockcertsSignupStage.class).setDone();

    final Response response = new MeController().blockcerts(req, this.currentUser);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
  }

  @Test
  public void parentPhoneDataNull() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    final Response response = new MeController().parentphone(req, this.currentUser, null);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasPhoneFieldEmpty() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("");

    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasPhoneFieldNull() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn(null);
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasNameFieldNull() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("+15005550006");
    Mockito.when(data.getName()).thenReturn(null);
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasNameFieldEmpty() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("+15005550006");
    Mockito.when(data.getName()).thenReturn("");
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasSurnameFieldNull() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("+15005550006");
    Mockito.when(data.getName()).thenReturn("name");
    Mockito.when(data.getSurname()).thenReturn(null);
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void parentPhoneDataHasSurnameFieldEmpty() {
    final ParentConfirmationPhone data = Mockito.mock(ParentConfirmationPhone.class);
    Mockito.when(data.getPhoneNumber()).thenReturn("+15005550006");
    Mockito.when(data.getName()).thenReturn("name");
    Mockito.when(data.getSurname()).thenReturn("");
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void completeParentPhoneStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);

    final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, "name", "surname");

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    final User returnedUser = (User) response.getEntity();
    final ParentPhoneSignupStage savedStage = returnedUser.getStage(ParentPhoneSignupStage.class);

    assertThat(savedStage.isDone()).isFalse();
    assertThat(savedStage.getData().getPhoneNumber()).isEqualTo(data.getPhoneNumber());
    assertThat(savedStage.getData().getTsExpiration()).isGreaterThan(new DateTime().plusDays(6).getMillis());
    assertThat(savedStage.getData().getTsExpiration()).isLessThan(new DateTime().plusDays(8).getMillis());
  }

  @Test
  public void doubleParentPhoneStage() {
    final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
    PowerMockito.mockStatic(RequestUtils.class);
    PowerMockito.when(RequestUtils.getHost(req)).thenReturn(HOST);

    final ParentConfirmationPhone data = new ParentConfirmationPhone("+15005550006", HOST, "name", "surname");

    new MeController().parentphone(req, this.currentUser, data);

    this.currentUser.getStage(ParentPhoneSignupStage.class).setDone();

    final Response response = new MeController().parentphone(req, this.currentUser, data);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CONFLICT);
  }

}
