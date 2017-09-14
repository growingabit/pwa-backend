package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class BitcoinAddressValidatorTest {

  @Test
  public void validateAddress() {
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i")).isTrue();
    assertThat(BitcoinAddressValidator.validate("1Q1pE5vPGEEMqRcVRMbtBK842Y6Pzo6nK9")).isTrue();
  }

  @Test
  public void doNotValidateInvalidAddress() {
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62X")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1ANNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1A Na15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62!")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62iz")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62izz")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1Q1pE5vPGEEMqRcVRMbtBK842Y6Pzo6nJ9")).isFalse();
    assertThat(BitcoinAddressValidator.validate("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62I")).isFalse();
    assertThat(BitcoinAddressValidator.validate("BZbvjr")).isFalse();
    assertThat(BitcoinAddressValidator.validate("i55j")).isFalse();
  }

  @Test
  public void shoudNotValidateNull() {
    assertThat(BitcoinAddressValidator.validate(null)).isFalse();
  }

  @Test
  public void shoudNotValidateEmptyString() {
    assertThat(BitcoinAddressValidator.validate("")).isFalse();
  }
}
