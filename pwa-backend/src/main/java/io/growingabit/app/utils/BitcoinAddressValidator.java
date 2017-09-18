package io.growingabit.app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class BitcoinAddressValidator {

  private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

  public static boolean isValid(final String addr) {
    if (StringUtils.isEmpty(addr) || addr.length() < 26 || addr.length() > 35) {
      return false;
    }

    final byte[] decoded = decodeBase58(addr, 25);
    if (decoded == null) {
      return false;
    }

    final byte[] hash1 = sha256(Arrays.copyOfRange(decoded, 0, 21));
    final byte[] hash2 = sha256(hash1);

    return Arrays.equals(Arrays.copyOfRange(hash2, 0, 4), Arrays.copyOfRange(decoded, 21, 25));
  }

  private static byte[] decodeBase58(final String input, final int len) {
    final byte[] output = new byte[len];
    for (final char t : input.toCharArray()) {
      int p = ALPHABET.indexOf(t);
      if (p == -1) {
        return null;
      }
      for (int j = len - 1; j >= 0; j--) {
        p += 58 * (output[j] & 0xFF);
        output[j] = (byte) (p % 256);
        p /= 256;
      }
      if (p != 0) {
        return null;
      }
    }

    return output;
  }

  private static byte[] sha256(final byte[] data) {
    try {
      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(data);
      return md.digest();
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
