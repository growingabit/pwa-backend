package io.growingabit.app.utils.auth;

import static com.google.common.truth.Truth.assertThat;

import io.growingabit.app.utils.auth.Auth0UserProfile;
import io.growingabit.app.utils.auth.Authorizer;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class AuthorizerTest {

  @Test
  public void principalGetNameShouldReturnUsername(){
    String userid = "0";
    Set<String> roles = null;
    String username = "username";
    boolean isSecure = false;
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.getUserPrincipal().getName()).isEqualTo(username);
  }

  @Test
  public void principalGetUserIDShouldReturnUserId(){
    String userid = "0";
    Set<String> roles = null;
    String username = "username";
    boolean isSecure = false;
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(((Auth0UserProfile)authorizer.getUserPrincipal()).getUserID()).isEqualTo(userid);
  }

  @Test
  public void isSecureShouldReturnTrue(){
    String userid = "0";
    Set<String> roles = null;
    String username = null;
    boolean isSecure = true;
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.isSecure()).isTrue();
  }

  @Test
  public void isSecureShouldReturnFalse(){
    String userid = "0";
    Set<String> roles = null;
    String username = null;
    boolean isSecure = false;
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.isSecure()).isFalse();
  }

  @Test
  public void authenticationSchemeIsForm(){
    String userid = "0";
    Set<String> roles = null;
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.getAuthenticationScheme()).isEqualTo(authenticationScheme);
  }

  @Test
  public void userShouldBeInRole(){
    String userid = "0";
    String checkRole = "checkRole";
    Set<String> roles = new HashSet<>();
    roles.add(checkRole);
    roles.add("otherRole");
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.isUserInRole(checkRole)).isTrue();
  }

  @Test
  public void userShouldNotBeInRole(){
    String userid = "0";
    String checkRole = "checkRole";
    Set<String> roles = new HashSet<>();
    roles.add("otherRole");
    String username = null;
    boolean isSecure = false;
    String authenticationScheme = "FORM";
    Authorizer authorizer = new Authorizer(userid, roles, username, isSecure );
    assertThat(authorizer.isUserInRole(checkRole)).isFalse();
  }
}
