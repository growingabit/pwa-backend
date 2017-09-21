package io.growingabit.app.utils.auth;

import static com.google.common.truth.Truth.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class AuthorizerTest {

  @Test
  public void principalGetNameShouldReturnUsername() {
    final String userid = "0";
    final Set<String> roles = null;
    final String username = "username";
    final boolean isSecure = false;
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.getUserPrincipal().getName()).isEqualTo(username);
  }

  @Test
  public void principalGetUserIDShouldReturnUserId() {
    final String userid = "0";
    final Set<String> roles = null;
    final String username = "username";
    final boolean isSecure = false;
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(((Auth0UserProfile) authorizer.getUserPrincipal()).getUserID()).isEqualTo(userid);
  }

  @Test
  public void isSecureShouldReturnTrue() {
    final String userid = "0";
    final Set<String> roles = null;
    final String username = null;
    final boolean isSecure = true;
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.isSecure()).isTrue();
  }

  @Test
  public void isSecureShouldReturnFalse() {
    final String userid = "0";
    final Set<String> roles = null;
    final String username = null;
    final boolean isSecure = false;
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.isSecure()).isFalse();
  }

  @Test
  public void authenticationSchemeIsForm() {
    final String userid = "0";
    final Set<String> roles = null;
    final String username = null;
    final boolean isSecure = false;
    final String authenticationScheme = "FORM";
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.getAuthenticationScheme()).isEqualTo(authenticationScheme);
  }

  @Test
  public void userShouldBeInRole() {
    final String userid = "0";
    final String checkRole = "checkRole";
    final Set<String> roles = new HashSet<>();
    roles.add(checkRole);
    roles.add("otherRole");
    final String username = null;
    final boolean isSecure = false;
    final String authenticationScheme = "FORM";
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.isUserInRole(checkRole)).isTrue();
  }

  @Test
  public void userShouldNotBeInRole() {
    final String userid = "0";
    final String checkRole = "checkRole";
    final Set<String> roles = new HashSet<>();
    roles.add("otherRole");
    final String username = null;
    final boolean isSecure = false;
    final String authenticationScheme = "FORM";
    final Authorizer authorizer = new Authorizer(userid, roles, username, isSecure);
    assertThat(authorizer.isUserInRole(checkRole)).isFalse();
  }
}
