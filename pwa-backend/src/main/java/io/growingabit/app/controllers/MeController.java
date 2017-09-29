package io.growingabit.app.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.NotFoundException;

import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.BitcoinAddress;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.ParentConfirmationPhone;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationBlockcerts;
import io.growingabit.app.model.StudentConfirmationEmail;
import io.growingabit.app.model.StudentConfirmationPhone;
import io.growingabit.app.model.StudentData;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;
import io.growingabit.app.utils.RequestUtils;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.jersey.annotations.Secured;

@Secured
@Path("api/v1/me")
public class MeController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCurrenUserInfo(@Context final User currentUser) {
    return Response.ok().entity(currentUser).build();
  }

  @POST
  @Path("/invitationcode")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response confirmInvitationCode(@Context final User currentUser, final Invitation i) {

    final InvitationCodeSignupStage userSignupStage = currentUser.getStage(InvitationCodeSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    try {
      final Invitation invitation = new InvitationDao().findByInvitationCode(i.getInvitationCode());
      final InvitationCodeSignupStage signupStage = new InvitationCodeSignupStage();
      signupStage.setData(invitation);
      signupStage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final NotFoundException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Invitation code not found").build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/studentdata")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response studentData(@Context final User currentUser, final StudentData data) {

    final StudentDataSignupStage userSignupStage = currentUser.getStage(StudentDataSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    if (data == null) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final StudentDataSignupStage stage = new StudentDataSignupStage();
      stage.setData(data);
      stage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/studentemail")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response studentemail(@Context final HttpServletRequest req, @Context final User currentUser, final StudentConfirmationEmail studentConfirmationEmail) {

    final StudentEmailSignupStage userSignupStage = currentUser.getStage(StudentEmailSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    if (studentConfirmationEmail == null || StringUtils.isEmpty(studentConfirmationEmail.getEmail())) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final StudentEmailSignupStage stage = new StudentEmailSignupStage();
      stage.setData(new StudentConfirmationEmail(studentConfirmationEmail.getEmail(), RequestUtils.getOrigin(req)));
      stage.exec(new SignupStageExecutor(currentUser));

      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/walletsetup")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response walletSetup(@Context final User currentUser, final BitcoinAddress address) {

    final WalletSetupSignupStage userSignupStage = currentUser.getStage(WalletSetupSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    if (address == null) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final WalletSetupSignupStage stage = new WalletSetupSignupStage();
      stage.setData(address);
      stage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
  }

  @POST
  @Path("/studentphone")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response studentphone(@Context final HttpServletRequest request, @Context final User currentUser, final StudentConfirmationPhone studentConfirmationPhone) {

    final StudentPhoneSignupStage userSignupStage = currentUser.getStage(StudentPhoneSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    if (studentConfirmationPhone == null || StringUtils.isEmpty(studentConfirmationPhone.getPhoneNumber())) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final StudentPhoneSignupStage stage = new StudentPhoneSignupStage();
      stage.setData(new StudentConfirmationPhone(studentConfirmationPhone.getPhoneNumber()));
      stage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("/blockcerts")
  @Produces(MediaType.APPLICATION_JSON)
  public Response blockcerts(@Context final HttpServletRequest req, @Context final User currentUser) {

    final StudentBlockcertsSignupStage userSignupStage = currentUser.getStage(StudentBlockcertsSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    try {
      final StudentConfirmationBlockcerts studentConfirmationBlockcerts = new StudentConfirmationBlockcerts(RequestUtils.getOrigin(req), currentUser.getId());
      final StudentBlockcertsSignupStage stage = new StudentBlockcertsSignupStage();
      stage.setData(studentConfirmationBlockcerts);
      stage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }

  }

  @POST
  @Path("/parentphone")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response parentphone(@Context final HttpServletRequest request, @Context final User currentUser, final ParentConfirmationPhone parentConfirmationPhone) {

    final ParentPhoneSignupStage userSignupStage = currentUser.getStage(ParentPhoneSignupStage.class);
    if (userSignupStage != null && userSignupStage.isDone()) {
      return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }

    if (parentConfirmationPhone == null || StringUtils.isEmpty(parentConfirmationPhone.getPhoneNumber()) || StringUtils.isEmpty(parentConfirmationPhone.getName()) || StringUtils.isEmpty(parentConfirmationPhone.getSurname())) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {
      final ParentPhoneSignupStage stage = new ParentPhoneSignupStage();
      stage.setData(new ParentConfirmationPhone(parentConfirmationPhone.getPhoneNumber(), RequestUtils.getOrigin(request), parentConfirmationPhone.getName(), parentConfirmationPhone.getSurname()));
      stage.exec(new SignupStageExecutor(currentUser));
      return Response.ok().entity(currentUser).build();
    } catch (final SignupStageExecutionException e) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
    }

  }

}
