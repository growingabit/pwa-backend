package io.growingabit.app.signup.executors;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import io.growingabit.app.dao.InvitationCodeSignupStageDao;
import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.InvitationCodeSignupStage;
import io.growingabit.app.model.User;
import io.growingabit.backoffice.dao.InvitationDao;
import io.growingabit.backoffice.model.Invitation;

class InvitationCodeSignupStageExecutor {

  private final InvitationDao invitationDao;
  private final InvitationCodeSignupStageDao invitationCodeSignupStageDao;

  public InvitationCodeSignupStageExecutor() {
    this.invitationDao = new InvitationDao();
    this.invitationCodeSignupStageDao = new InvitationCodeSignupStageDao();
  }

  public void exec(final InvitationCodeSignupStage stage, final User user) throws SignupStageExecutionException {
    Preconditions.checkNotNull(stage);
    Preconditions.checkNotNull(user);

    final Invitation invitation = stage.getData();
    Preconditions.checkNotNull(invitation);
    try {
      ObjectifyService.ofy().transact(new VoidWork() {
        @Override
        public void vrun() {
          if (invitation.isConfirmed()) {
            throw new SignupStageExecutionException("Invitation code already confirmed");
          } else {
            invitation.setConfirmed();
            invitation.setRelatedUserWebSafeKey(Key.create(user));
            InvitationCodeSignupStageExecutor.this.invitationDao.persist(invitation);

            final InvitationCodeSignupStage userSignupStage = user.getMandatorySignupStage(InvitationCodeSignupStage.class);

            userSignupStage.setData(invitation);
            userSignupStage.setDone();
            InvitationCodeSignupStageExecutor.this.invitationCodeSignupStageDao.persist(userSignupStage);
          }
        }
      });
    } catch (final RuntimeException e) {
      if (e instanceof SignupStageExecutionException) {
        throw e;
      } else {
        throw new SignupStageExecutionException(e);
      }

    }
  }

}
