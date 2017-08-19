package io.growingabit.app.utils;

import static com.google.common.truth.Truth.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class AuthorizerTest {

  @Test
  public void principalGetNameShouldReturnUsername(){
    Set<String> roles = null;
    String username = "username";
    boolean isSecure = false;
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.getUserPrincipal().getName()).isEqualTo(username);
  }

  @Test
  public void isSecureShouldReturnTrue(){
    Set<String> roles = null;
    String username = null;
    boolean isSecure = true;
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.isSecure()).isTrue();
  }

  @Test
  public void isSecureShouldReturnFalse(){
    Set<String> roles = null;
    String username = null;
    boolean isSecure = false;
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.isSecure()).isFalse();
  }

  @Test
  public void authenticationSchemeIsForm(){
    Set<String> roles = null;
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.getAuthenticationScheme()).isEqualTo(authenticationScheme);
  }

  @Test
  public void userShouldBeInRole(){
    String checkRole = "checkRole";
    Set<String> roles = new HashSet<>();
    roles.add(checkRole);
    roles.add("otherRole");
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.isUserInRole(checkRole)).isTrue();
  }

  @Test
  public void userShouldNotBeInRole(){
    String checkRole = "checkRole";
    Set<String> roles = new HashSet<>();
    roles.add("otherRole");
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(roles, username, isSecure );
    assertThat(authorizer.isUserInRole(checkRole)).isFalse();
  }
}
