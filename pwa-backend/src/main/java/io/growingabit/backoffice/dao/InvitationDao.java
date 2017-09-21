package io.growingabit.backoffice.dao;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;

import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.dao.BaseDao;

public class InvitationDao extends BaseDao<Invitation> {

  public InvitationDao() {
    super(Invitation.class);
  }

  public Invitation findByInvitationCode(final String invitationCode) {
    Preconditions.checkArgument(invitationCode != null, "invitationCode cannot be null");
    return this.find(Key.create(Invitation.class, invitationCode));
  }
}
