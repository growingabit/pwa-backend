package io.growingabit.app.dao;

import io.growingabit.app.model.User;
import io.growingabit.common.dao.BaseDao;

public class UserDao extends BaseDao<User> {

  public UserDao() {
    super(User.class);
  }
}
