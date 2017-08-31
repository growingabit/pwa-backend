package io.growingabit.jersey.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class UserRolesTest {

  @Test
  public void neverGenerateNull() {
    assertThat(UserRoles.ADMIN_ROLE).isEqualTo("admin");
  }

}
