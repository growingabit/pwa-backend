package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class SecureStringGeneratorTest {

  private SecureStringGenerator secureStringGenerator;
  private int stringLenght;

  @Before
  public void setup() {
    this.stringLenght = new Random().nextInt(100);
    this.secureStringGenerator = new SecureStringGenerator(this.stringLenght);
  }

  @Test
  public void neverGenerateNull() {
    int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(secureStringGenerator.nextString()).isNotNull();
    }
  }

  @Test
  public void alwaysGenerateFixedLenghtString() {
    int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(secureStringGenerator.nextString().length()).isEqualTo(this.stringLenght);
    }
  }

  @Test
  public void doesNotContainsReservedChars() {
    int n = new Random().nextInt(1000);
    String generatedString;
    for (int i = 0; i < n; i++) {
      generatedString = secureStringGenerator.nextString();
      assertThat(generatedString).doesNotMatch(".*[!*'();:@&=+$,/?#\\[\\]].*");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void lenghtMustBeGreatherThanZero() {
    new SecureStringGenerator(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lenghtMustBeNotNegative() {
    int n = new Random().nextInt(1000);
    new SecureStringGenerator(-1*n);
  }

}