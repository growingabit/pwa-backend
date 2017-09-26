package io.growingabit.app.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.jersey.annotations.Secured;

@Secured
@Path("api/v1/verify/phone/{verificationCode}")
public class VerificationPhoneController {

  private static final XLogger log = XLoggerFactory.getXLogger(VerificationPhoneController.class);

  @GET
  public Response verifyPhone(@Context final User currentUser, @PathParam("verificationCode") final String verificationCode) {
    final StudentPhoneSignupStage stage = currentUser.getStage(StudentPhoneSignupStage.class);

    final long now = new DateTime().getMillis();
    if (!stage.getData().getVerificationCode().equals(verificationCode) || stage.getData().getTsExpiration() < now) {
      return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
    }

    stage.setDone();
    new StudentPhoneSignupStageDao().persist(stage);

    return Response.ok().build();
  }
}
