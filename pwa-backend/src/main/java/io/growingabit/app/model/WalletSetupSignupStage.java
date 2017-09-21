package io.growingabit.app.model;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import io.growingabit.app.exceptions.SignupStageExecutionException;
import io.growingabit.app.model.base.SignupStage;
import io.growingabit.app.signup.executors.SignupStageExecutor;

@Entity
@Cache
public class WalletSetupSignupStage extends SignupStage<BitcoinAddress> {

  private BitcoinAddress bitcoinAddress;

  @Override
  public BitcoinAddress getData() {
    return this.bitcoinAddress;
  }

  @Override
  public void setData(final BitcoinAddress bitcoinAddress) {
    Preconditions.checkArgument(bitcoinAddress != null);
    this.bitcoinAddress = bitcoinAddress;
  }

  @Override
  public void exec(final SignupStageExecutor executor) throws SignupStageExecutionException {
    executor.exec(this);
  }
}
