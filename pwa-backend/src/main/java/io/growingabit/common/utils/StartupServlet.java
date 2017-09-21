package io.growingabit.common.utils;

import javax.servlet.http.HttpServlet;

import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.ParentPhoneSignupStage;
import io.growingabit.app.model.StudentBlockcertsOTPSignupStage;
import io.growingabit.app.model.StudentDataSignupStage;
import io.growingabit.app.model.StudentEmailSignupStage;
import io.growingabit.app.model.StudentPhoneSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.app.model.WalletSetupSignupStage;
import io.growingabit.backoffice.model.Invitation;

public class StartupServlet extends HttpServlet {

  private static final long serialVersionUID = -4243709944154393569L;

  static {
    ObjectifyService.factory().register(User.class);
    ObjectifyService.factory().register(Invitation.class);
    ObjectifyService.factory().register(InvitationCodeSignupStage.class);
    ObjectifyService.factory().register(StudentDataSignupStage.class);
    ObjectifyService.factory().register(StudentEmailSignupStage.class);
    ObjectifyService.factory().register(StudentPhoneSignupStage.class);
    ObjectifyService.factory().register(WalletSetupSignupStage.class);
    ObjectifyService.factory().register(StudentBlockcertsOTPSignupStage.class);
    ObjectifyService.factory().register(ParentPhoneSignupStage.class);

    SignupStageFactory.registerMandatory(InvitationCodeSignupStage.class);

    SignupStageFactory.register(StudentDataSignupStage.class);
    SignupStageFactory.register(StudentEmailSignupStage.class);
    SignupStageFactory.register(StudentPhoneSignupStage.class);
    SignupStageFactory.register(WalletSetupSignupStage.class);
    SignupStageFactory.register(StudentBlockcertsOTPSignupStage.class);
    SignupStageFactory.register(ParentPhoneSignupStage.class);
  }
}
