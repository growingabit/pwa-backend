package io.growingabit.backoffice.dao;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.ObjectifyService;
import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.dao.BaseDao;

public class InvitationDao extends BaseDao<Invitation> {

  public InvitationDao() {
    super(Invitation.class);
  }

  public Invitation findByInvitationCode(final String invitationCode) {
    Preconditions.checkArgument(invitationCode != null, "invitationCode cannot be null");
    return ObjectifyService.ofy().load().type(Invitation.class).filter("invitationCode", invitationCode).first().safe();
  }
}
