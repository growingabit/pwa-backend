package io.growingabit.app.utils;

import com.google.common.base.Preconditions;
import java.security.SecureRandom;
import java.util.Random;

public class SecureStringGenerator {

  // only use unreserved url characters
  private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.~-";

  private final Random random = new SecureRandom();
  private final char[] buf;

  public SecureStringGenerator(int length){
    Preconditions.checkArgument(length > 0, "Length should be greater than 0");
    buf = new char[length];
  }

  public String nextString(){
    for (int idx = 0; idx < buf.length; ++idx){
      buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
    }
    return new String(buf);
  }
}
