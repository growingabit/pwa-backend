package io.growingabit.backoffice.dao;

import io.growingabit.backoffice.model.Invitation;
import io.growingabit.common.dao.BaseDao;

public class InvitationDao extends BaseDao<Invitation> {

  public InvitationDao() {
    super(Invitation.class);
  }
}
