package io.growingabit.backoffice.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.User;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.testUtils.BaseGaeTest;
import org.junit.Before;
import org.junit.Test;

public class InvitationTest extends BaseGaeTest {

  private InvitationDao dao;
  private UserDao userDao;

  @Before
  public void setUp() {
    ObjectifyService.register(Invitation.class);
    ObjectifyService.register(User.class);
    this.dao = new InvitationDao();
    this.userDao = new UserDao();
  }

  @Test
  public void confirmedInitValue() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
    assertThat(invitation.isConfirmed()).isFalse();
  }

  @Test
  public void requiredFields() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
    assertThat(invitation.getCreationDate()).isGreaterThan(0L);
  }

  @Test(expected = SaveException.class)
  public void missingSchoolRequiredField() {
    final Invitation invitation = new Invitation(null, "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingClassRequiredField() {
    final Invitation invitation = new Invitation("My school", null, "This Year", "My Spec");
    this.dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingYearRequiredField() {
    final Invitation invitation = new Invitation("My school", "My class", null, "My Spec");
    this.dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingSpecRequiredField() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", null);
    this.dao.persist(invitation);
  }

  @Test
  public void checkInvitationCode() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
    assertThat(invitation.getInvitationCode().length()).isEqualTo(7);
  }

  @Test
  public void getUserShouldReturnNull() {
    final Invitation invitation = new Invitation();
    final String s = invitation.getRelatedUserWebSafeKey();
    assertThat(s).isNull();
  }

  @Test
  public void setUserShouldNotAcceptNull() {
    final Invitation invitation = new Invitation();
    final User u = new User();
    u.setId("id");
    this.userDao.persist(u);
    invitation.setRelatedUserWebSafeKey(Key.create(u));
    invitation.setRelatedUserWebSafeKey(null);
    final String s = invitation.getRelatedUserWebSafeKey();
    assertThat(s).isNotNull();
  }

  @Test
  public void shouldNotAcceptNullSchool() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchool(null);
    assertThat(invitation.getSchool()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptNullSchoolClass() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchoolClass(null);
    assertThat(invitation.getSchoolClass()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptNullYear() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchoolYear(null);
    assertThat(invitation.getSchoolYear()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptNullSpecialization() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSpecialization(null);
    assertThat(invitation.getSpecialization()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptNullRelatedUserWebSafeKey() {
    final User user = new User();
    user.setId("Id");
    this.userDao.persist(user);

    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setRelatedUserWebSafeKey(Key.create(user));
    invitation.setSpecialization(null);
    assertThat(invitation.getSpecialization()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptySchool() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchool("");
    assertThat(invitation.getSchool()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptySchoolClass() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchoolClass("");
    assertThat(invitation.getSchoolClass()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptyYear() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSchoolYear("");
    assertThat(invitation.getSchoolYear()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptySpecialization() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setSpecialization("");
    assertThat(invitation.getSpecialization()).isNotEmpty();
  }

  @Test
  public void shouldNotAcceptEmptyRelatedUserWebSafeKey() {
    final User user = new User();
    user.setId("Id");
    this.userDao.persist(user);

    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setRelatedUserWebSafeKey(Key.create(user));
    invitation.setSpecialization("");
    assertThat(invitation.getSpecialization()).isNotEmpty();
  }

}
