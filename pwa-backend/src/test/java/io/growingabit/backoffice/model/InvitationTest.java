package io.growingabit.backoffice.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.testUtils.BaseDatastoreTest;

public class InvitationTest extends BaseDatastoreTest {

  private InvitationDao dao;

  @Before
  public void setUp() {
    ObjectifyService.register(Invitation.class);
    this.dao = new InvitationDao();
  }

  @Test
  public void confirmedInitValue() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    dao.persist(invitation);
    assertThat(invitation.isConfirmed()).isFalse();
  }

  @Test
  public void requiredFields() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    dao.persist(invitation);
    assertThat(invitation.getCreationDate()).isGreaterThan(0L);
  }

  @Test(expected = SaveException.class)
  public void missingSchoolRequiredField() {
    Invitation invitation = new Invitation(null, "My class", "This Year", "My Spec");
    dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingClassRequiredField() {
    Invitation invitation = new Invitation("My school", null, "This Year", "My Spec");
    dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingYearRequiredField() {
    Invitation invitation = new Invitation("My school", "My class", null, "My Spec");
    dao.persist(invitation);
  }

  @Test(expected = SaveException.class)
  public void missingSpecRequiredField() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", null);
    dao.persist(invitation);
  }

  @Test
  public void checkInvitationCode() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    dao.persist(invitation);
    assertThat(invitation.getInvitationCode().length()).isEqualTo(7);;
  }

}
