package io.growingabit.app.controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.googlecode.objectify.Key;

import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.jersey.annotations.Secured;

@Path("api/v1/verificationemail")
public class VerificationEmailController {

  private final static XLogger log = XLoggerFactory.getXLogger(VerificationEmailController.class);

  @Context
  private SecurityContext securityContext;

  private User getCurrentUser(final SecurityContext securityContext) {
    final Auth0UserProfile auth0User = (Auth0UserProfile) securityContext.getUserPrincipal();
    return new UserDao().find(Key.create(User.class, auth0User.getUserID()));
  }


  @Secured
  @Path("{verificationCode}")
  @GET
  public Response verifyEmail(@PathParam("verificationCode") String verificationCode) {

    log.entry(verificationCode);

    try {
      verificationCode = new String(Base64.decodeBase64(verificationCode), "utf-8");
      User user = this.getCurrentUser(securityContext);

      final String signupStageIndentifier = Settings.getConfig().getString(StudentEmailSignupStage.class.getCanonicalName());
      final StudentEmailSignupStage stage = (StudentEmailSignupStage) user.getSignupStages().get(signupStageIndentifier).get();

      if (!stage.getData().getVerificationCode().equalsIgnoreCase(verificationCode) || stage.getData().getTsExpiration() > new DateTime().plusDays(7).getMillis()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      new StudentEmailSignupStageDao().persist(stage);

      return Response.ok().build();

    } catch (UnsupportedEncodingException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

  }


}
