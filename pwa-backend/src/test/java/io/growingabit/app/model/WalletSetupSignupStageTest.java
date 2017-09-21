package io.growingabit.app.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

public class WalletSetupSignupStageTest {


  private WalletSetupSignupStage stage;
  private BitcoinAddress validBitcoinAddress;

  @Before
  public void setup() {
    this.stage = new WalletSetupSignupStage();
    this.validBitcoinAddress = new BitcoinAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i");
  }

  @Test
  public void validSignupStage() {
    this.stage.setData(this.validBitcoinAddress);
    assertThat(this.stage.getData()).isEqualTo(this.validBitcoinAddress);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notAcceptNullBitcoinAddress() {
    this.stage.setData(null);
  }

}
