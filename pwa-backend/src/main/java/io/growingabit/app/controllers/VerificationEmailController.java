package io.growingabit.app.controllers;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.jersey.annotations.Secured;
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

@Path("api/v1/verify/email/{verificationCode}")
public class VerificationEmailController {

  private final static XLogger log = XLoggerFactory.getXLogger(VerificationEmailController.class);

  @Secured
  @GET
  public Response verifyEmail(@Context final User currentUser, @PathParam("verificationCode") String verificationCode) {

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


}
