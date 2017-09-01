package io.growingabit.app.utils;

import java.security.SecureRandom;
import java.util.Random;

import com.google.common.base.Preconditions;

public class SecureStringGenerator {

  // only use unreserved url characters
  private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.~-";

  private final Random random = new SecureRandom();
  private final char[] buf;

  public SecureStringGenerator(final int length) {
    Preconditions.checkArgument(length > 0, "Length should be greater than 0");
    this.buf = new char[length];
  }

  public String nextString() {
    for (int idx = 0; idx < this.buf.length; idx++) {
      this.buf[idx] = symbols.charAt(this.random.nextInt(symbols.length()));
    }
    return new String(this.buf);
  }
}
