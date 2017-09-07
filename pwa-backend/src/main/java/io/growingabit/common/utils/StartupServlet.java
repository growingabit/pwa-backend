package io.growingabit.common.utils;

import com.googlecode.objectify.ObjectifyService;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.backoffice.model.Invitation;
import javax.servlet.http.HttpServlet;

public class StartupServlet extends HttpServlet {

  private static final long serialVersionUID = -4243709944154393569L;

  static {
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);

    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);
  }
}
