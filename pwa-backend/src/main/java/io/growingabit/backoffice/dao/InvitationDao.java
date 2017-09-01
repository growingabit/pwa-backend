package io.growingabit.backoffice.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.SaveException;

import io.growingabit.app.dao.BaseDao;
import io.growingabit.backoffice.model.Invitation;

public class InvitationDao extends BaseDao<Invitation> {

  public InvitationDao() {
    super(Invitation.class);
  }

  @Override
  public Key<Invitation> persist(Invitation entity) throws SaveException {
    return super.persist(entity);
  }

}
