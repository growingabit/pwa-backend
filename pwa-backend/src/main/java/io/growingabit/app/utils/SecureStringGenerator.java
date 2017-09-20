package io.growingabit.app.utils;

import com.google.common.base.Preconditions;
import java.security.SecureRandom;
import java.util.Random;

public class SecureStringGenerator {

  // only use unreserved url characters
  private static final String ALL_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.~-";
  private static final String NUMERIC_SYMBOLS = "0123456789";

  private final Random random = new SecureRandom();
  private final char[] buf;

  public SecureStringGenerator(final int length) {
    Preconditions.checkArgument(length > 0, "Length should be greater than 0");
    this.buf = new char[length];
  }

  public String nextString() {
    return this.generate(ALL_SYMBOLS);
  }

  public String nextNumericString() {
    return this.generate(NUMERIC_SYMBOLS);
  }

  private String generate(final String symbols) {
    for (int idx = 0; idx < this.buf.length; idx++) {
      this.buf[idx] = symbols.charAt(this.random.nextInt(symbols.length()));
    }
    return new String(this.buf);
  }
}
