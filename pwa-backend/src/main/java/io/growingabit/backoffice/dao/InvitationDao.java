package io.growingabit.backoffice.dao;

import io.growingabit.app.dao.BaseDao;
import io.growingabit.backoffice.model.Invitation;

public class InvitationDao extends BaseDao<Invitation> {

  public InvitationDao() {
    super(Invitation.class);
  }
}
