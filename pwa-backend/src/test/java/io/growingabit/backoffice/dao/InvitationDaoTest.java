package io.growingabit.backoffice.dao;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseGaeTest;

public class InvitationDaoTest extends BaseGaeTest {

  private InvitationDao dao;

  @Before
  public void setUp() {
    ObjectifyService.register(Invitation.class);
    this.dao = new InvitationDao();
  }

  @Test
  public void persit() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
    final Invitation foundedInvitation = this.dao.find(invitation.getWebSafeKey());
    assertThat(invitation).isNotNull();
  }

  @Test(expected = SaveException.class)
  public void persitFail() {
    this.dao.persist(new Invitation());
  }

  @Test
  public void retrieveByInvitationCode() {
    final Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    this.dao.persist(invitation);
    final String invitationCode = invitation.getInvitationCode();
    final Invitation foundedInvitation = this.dao.findByInvitationCode(invitationCode);
    assertThat(invitation).isNotNull();
  }

  @Test(expected = NotFoundException.class)
  public void throwExceptionIfNotFound() {
    this.dao.findByInvitationCode("inexistent");
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwExceptionIfNull() {
    this.dao.findByInvitationCode(null);
  }
}
