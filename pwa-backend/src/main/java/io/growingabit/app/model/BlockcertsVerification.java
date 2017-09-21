package io.growingabit.app.model;

public class BlockcertsVerification {

  private String bitcoinAddress;
  transient String nonce;

  public String getBitcoinAddress() {
    return this.bitcoinAddress;
  }

  public String getNonce() {
    return this.nonce;
  }
}
