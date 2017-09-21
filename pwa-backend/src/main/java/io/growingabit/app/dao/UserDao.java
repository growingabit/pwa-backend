package io.growingabit.app.dao;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import io.growingabit.app.model.User;
import io.growingabit.common.dao.BaseDao;

public class UserDao extends BaseDao<User> {

  public UserDao() {
    super(User.class);
  }

  public boolean exist(final Key<User> userKey) throws IllegalArgumentException {
    Preconditions.checkArgument(userKey != null, "userKey cannot be null");
    return ObjectifyService.ofy().load().key(userKey).now() != null;
  }
}
