package io.growingabit.app.dao;

import io.growingabit.app.model.base.SignupStage;
import io.growingabit.common.dao.BaseDao;

public class GenericSignupStageDao extends BaseDao<SignupStage> {

  public GenericSignupStageDao() {
    super(SignupStage.class);
  }
}
