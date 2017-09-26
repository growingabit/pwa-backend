package io.growingabit.app.controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.ParentPhoneVerificationTaskData;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.gson.GsonFactory;

@Path("api/v1/verify/parentphone/{verificationCode}")
public class VerificationParentPhoneController {

  private static final XLogger log = XLoggerFactory.getXLogger(VerificationParentPhoneController.class);

  @GET
  public Response verifyPhone(@PathParam("verificationCode") final String verificationCode) {

    try {
      final String decodedCode = new String(Base64.decodeBase64(verificationCode), "utf-8");
      final ParentPhoneVerificationTaskData verificationTaskData = GsonFactory.getGsonInstance().fromJson(decodedCode, ParentPhoneVerificationTaskData.class);
      final User currentUser = new UserDao().find(verificationTaskData.getUserId());

      final ParentPhoneSignupStage stage = currentUser.getStage(ParentPhoneSignupStage.class);

      if (!stage.getData().getVerificationCode().equals(verificationTaskData.getVerificationCode()) || new DateTime().getMillis() > stage.getData().getTsExpiration()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      new ParentPhoneSignupStageDao().persist(stage);

      return Response.ok().build();
    } catch (final UnsupportedEncodingException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }
  }
}
