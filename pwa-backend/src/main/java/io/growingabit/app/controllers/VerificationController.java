package io.growingabit.app.controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import io.growingabit.app.dao.StudentBlockcertsOTPSignupStageDao;
import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.model.StudentBlockcertsOTPSignupStage;
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

      final DateTime dateTime = new DateTime();
      if (!stage.getData().getVerificationCode().equals(verificationCode) || stage.getData().getTsExpiration() > dateTime.plusDays(7).getMillis()) {
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

  @Secured
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

  @Secured
  @Path("blockcertsotp/{otp}")
  @GET
  public Response verifyBlockcertsOTP(@Context final User currentUser, @PathParam("otp") String otp) {

    log.entry(otp);

    try {
      otp = new String(Base64.decodeBase64(otp), "utf-8");

      final StudentBlockcertsOTPSignupStage stage = currentUser.getStage(StudentBlockcertsOTPSignupStage.class);

      final DateTime dateTime = new DateTime();
      if (!stage.getData().getOtp().equals(otp) || stage.getData().getTsExpiration() > dateTime.plusDays(7).getMillis()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      new StudentBlockcertsOTPSignupStageDao().persist(stage);

      return Response.ok().build();

    } catch (final UnsupportedEncodingException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

  }


}
