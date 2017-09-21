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
    this.stringLenght = new Random().nextInt(100) + 1;
    this.secureStringGenerator = new SecureStringGenerator(this.stringLenght);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lenghtMustBeGreatherThanZero() {
    new SecureStringGenerator(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lenghtMustBeNotNegative() {
    final int n = new Random().nextInt(1000);
    new SecureStringGenerator(-1 * n);
  }

  @Test
  public void neverGenerateNull() {
    final int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(this.secureStringGenerator.nextString()).isNotNull();
    }
  }

  @Test
  public void alwaysGenerateFixedLenghtString() {
    final int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(this.secureStringGenerator.nextString().length()).isEqualTo(this.stringLenght);
    }
  }

  @Test
  public void doesNotContainsReservedChars() {
    final int n = new Random().nextInt(1000);
    String generatedString;
    for (int i = 0; i < n; i++) {
      generatedString = this.secureStringGenerator.nextString();
      assertThat(generatedString).doesNotMatch(".*[!*'();:@&=+$,/?#\\[\\]].*");
    }
  }


  @Test
  public void numericNeverGenerateNull() {
    final int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(this.secureStringGenerator.nextNumericString()).isNotNull();
    }
  }

  @Test
  public void numericAlwaysGenerateFixedLenghtString() {
    final int n = new Random().nextInt(1000);
    for (int i = 0; i < n; i++) {
      assertThat(this.secureStringGenerator.nextNumericString().length()).isEqualTo(this.stringLenght);
    }
  }

  @Test
  public void numericContainsOnlyDigits() {
    final int n = new Random().nextInt(1000);
    String generatedString;
    for (int i = 0; i < n; i++) {
      generatedString = this.secureStringGenerator.nextNumericString();
      assertThat(generatedString).matches("[0-9]+");
    }
  }

}
