package io.growingabit.backoffice.model;

import static com.google.common.truth.Truth.assertThat;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.testUtils.BaseDatastoreTest;
import org.junit.Before;
import org.junit.Test;

public class InvitationTest extends BaseDatastoreTest {

  private InvitationDao dao;

  @Before
  public void setUp() {
    ObjectifyService.register(Invitation.class);
    this.dao = new InvitationDao();
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
  public void checkEmpyInvitationCode() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    invitation.setInvitationCode("");
    this.dao.persist(invitation);
    assertThat(invitation.getInvitationCode().length()).isEqualTo(7);
  }

  @Test
  public void doesNotModifyAlreadyDefinedInvitationCode() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    final String invitationCode = "1234567";
    invitation.setInvitationCode(invitationCode);
    this.dao.persist(invitation);
    assertThat(invitation.getInvitationCode()).isEqualTo(invitationCode);
  }
}
