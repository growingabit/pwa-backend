package io.growingabit.app.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.googlecode.objectify.Key;

import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.jersey.annotations.Secured;

@Secured
@Path("api/v1/verify/phone/{verificationCode}")
public class VerificationPhoneController {

  private static final XLogger log = XLoggerFactory.getXLogger(VerificationPhoneController.class);

  private User getCurrentUser(final SecurityContext securityContext) {
    final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
    return new UserDao().find(Key.create(User.class, auth0User.getUserID()));
  }

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
}
