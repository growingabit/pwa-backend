package io.growingabit.app.model;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public class BlockcertsOtp {

  private String bitcoinAddress;
  transient String nonce;

  private BlockcertsOtp() {}

  public BlockcertsOtp(final String address, ) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(address), "Bitcoin address should not be empty or null");
    this.address = address;
  }

  public String getAddress() {
    return this.address;
  }
}
