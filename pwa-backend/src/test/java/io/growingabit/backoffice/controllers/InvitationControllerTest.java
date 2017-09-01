package io.growingabit.backoffice.controllers;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseDatastoreTest;

public class InvitationControllerTest extends BaseDatastoreTest {

  private InvitationDao dao;

  @Before
  public void setUp() {
    ObjectifyService.register(Invitation.class);
    this.dao = new InvitationDao();
  }

  @Test
  public void listNotEmpty() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    dao.persist(invitation);
    Response response = new InvitationController().list();
    @SuppressWarnings("unchecked")
    List<Invitation> list = (List<Invitation>) response.getEntity();
    assertThat(list).isNotEmpty();
  }

  @Test
  public void listEmpty() {
    Response response = new InvitationController().list();
    @SuppressWarnings("unchecked")
    List<Invitation> list = (List<Invitation>) response.getEntity();
    assertThat(list).isEmpty();
  }

  @Test
  public void save() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
    Response response = new InvitationController().save(invitation);
    Invitation i = (Invitation) response.getEntity();
    assertThat(i).isEqualTo(invitation);
  }

  @Test
  public void saveFailWithOutSchol() {
    Invitation invitation = new Invitation(null, "My class", "This Year", "My Spec");
    Response response = new InvitationController().save(invitation);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void saveFailWithOutClass() {
    Invitation invitation = new Invitation("My school", null, "This Year", "My Spec");
    Response response = new InvitationController().save(invitation);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void saveFailWithOutYear() {
    Invitation invitation = new Invitation("My school", "My class", null, "My Spec");
    Response response = new InvitationController().save(invitation);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void saveFailWithOutSpecialization() {
    Invitation invitation = new Invitation("My school", "My class", "This Year", null);
    Response response = new InvitationController().save(invitation);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }


}