package io.growingabit.app.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.googlecode.objectify.Key;

import io.growingabit.app.dao.ParentPhoneSignupStageDao;
import io.growingabit.app.dao.StudentBlockcertsSignupStageDao;
import io.growingabit.app.dao.StudentEmailSignupStageDao;
import io.growingabit.app.dao.StudentPhoneSignupStageDao;
import io.growingabit.app.dao.UserDao;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.ParentPhoneVerificationTaskData;
import io.growingabit.app.model.StudentBlockcertsSignupStage;
import io.growingabit.app.model.StudentConfirmationBlockcerts;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.utils.BitcoinAddressValidator;
import io.growingabit.app.utils.gson.GsonFactory;
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

      if (!stage.getData().getVerificationCode().equals(verificationCode) || new DateTime().getMillis() > stage.getData().getTsExpiration()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      stage.getData().invalidVerificationCode();

      new StudentEmailSignupStageDao().persist(stage);

      log.exit(stage);

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

    if (!stage.getData().getVerificationCode().equals(verificationCode) || new DateTime().getMillis() > stage.getData().getTsExpiration()) {
      return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
    }

    stage.setDone();
    new StudentPhoneSignupStageDao().persist(stage);

    return Response.ok().build();
  }

  @Path("parentphone/{verificationCode}")
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

  @Path("blockcerts")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response verifyBlockcerts(StudentConfirmationBlockcerts studentConfirmationBlockcerts) {

    log.entry(studentConfirmationBlockcerts);

    if (!BitcoinAddressValidator.isValid(studentConfirmationBlockcerts.getBitcoinAddress())) {
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }

    try {

      List<String> list = Splitter.on(":").splitToList(new String(Base64.decodeBase64(studentConfirmationBlockcerts.getNonce()), "utf-8"));
      Preconditions.checkArgument(list.size() == 2);

      String userId = list.get(0);
      String hash = list.get(1);

      final User currentUser = new UserDao().find(Key.create(User.class, userId));
      final StudentBlockcertsSignupStage stage = currentUser.getStage(StudentBlockcertsSignupStage.class);
      StudentConfirmationBlockcerts sd = stage.getData();

      if (!StringUtils.left(new String(DigestUtils.sha1(sd.getUserId() + sd.getTsExpiration() + sd.getOrigin()), "utf-8"), 5).equals(hash) || new DateTime().getMillis() > sd.getTsExpiration()) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
      }

      stage.setDone();
      stage.getData().invalidNonce();
      stage.getData().setBitcoinAddress(studentConfirmationBlockcerts.getBitcoinAddress());

      new StudentBlockcertsSignupStageDao().persist(stage);

      log.exit(stage);

      return Response.ok().build();

    } catch (final IllegalArgumentException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
    } catch (final UnsupportedEncodingException e) {
      log.catching(e);
      return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }

  }


}
