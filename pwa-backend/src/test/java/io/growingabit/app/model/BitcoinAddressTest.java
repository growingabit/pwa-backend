package io.growingabit.app.model;

import org.junit.Test;

public class BitcoinAddressTest {

  @Test(expected = IllegalArgumentException.class)
  public void shoudNotAcceptNull() {
    new BitcoinAddress(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shoudNotAcceptEmptyString() {
    new BitcoinAddress("");
  }
}
