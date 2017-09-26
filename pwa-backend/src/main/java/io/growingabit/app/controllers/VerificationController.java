package io.growingabit.app.controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.googlecode.objectify.Key;

import io.growingabit.app.dao.StudentBlockcertsOTPSignupStageDao;
import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentBlockcertsOTPSignupStage;
import io.growingabit.app.model.StudentConfirmationBlockcertsOTP;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.jersey.annotations.Secured;

@Path("api/v1/verify")
public class VerificationController {

  private static final XLogger log = XLoggerFactory.getXLogger(VerificationController.class);

  @Secured
  @Path("email/{code}")
  @GET
  public Response verifyEmail(@Context final User currentUser, @PathParam("code") String verificationCode) {

    log.entry(verificationCode);

    try {
      verificationCode = new String(Base64.decodeBase64(verificationCode), "utf-8");

      final StudentEmailSignupStage stage = currentUser.getStage(StudentEmailSignupStage.class);

      final long now = new DateTime().getMillis();
      if (!stage.getData().getVerificationCode().equals(verificationCode) || stage.getData().getTsExpiration() < now) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      new StudentEmailSignupStageDao().persist(stage);

      return Response.ok().build();

    } catch (final UnsupportedEncodingException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

  }

  @Path("phone/{verificationCode}")
  @GET
  public Response verifyPhone(@Context final User currentUser, @PathParam("verificationCode") final String verificationCode) {
    final StudentPhoneSignupStage stage = currentUser.getStage(StudentPhoneSignupStage.class);

    final DateTime dateTime = new DateTime();
    if (!stage.getData().getVerificationCode().equals(verificationCode) || stage.getData().getTsExpiration() > dateTime.plusDays(7).getMillis()) {
      return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
    }

    stage.setDone();
    new StudentPhoneSignupStageDao().persist(stage);

    return Response.ok().build();
  }

  @Path("blockcerts")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response testPublic(StudentConfirmationBlockcertsOTP confirmation) {

    log.entry(confirmation);

    try {
      // TODO: fare hash per avee lo studente e lo stage
      // final StudentBlockcertsOTPSignupStage stage = currentUser.getStage(StudentBlockcertsOTPSignupStage.class);

      User user = new UserDao().find(Key.create(User.class, "google-oauth2|100230250104840641369"));
      final StudentBlockcertsOTPSignupStage stage = user.getStage(StudentBlockcertsOTPSignupStage.class);

      if (!stage.getData().getNonce().equals(confirmation.getNonce()) || new DateTime().getMillis() > stage.getData().getTsExpiration()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      log.info("Blockcerts verified");
      stage.setDone();
      stage.getData().invalidNonce();
      stage.getData().setBitcoinAddress(confirmation.getBitcoinAddress());

      new StudentBlockcertsOTPSignupStageDao().persist(stage);

      return Response.ok().build();

    } catch (final IllegalArgumentException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

  }


}
