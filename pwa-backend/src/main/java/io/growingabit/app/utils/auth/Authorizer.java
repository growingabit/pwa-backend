package io.growingabit.app.utils.auth;

import java.security.Principal;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

public class Authorizer implements SecurityContext {

  private Set<String> roles;
  private String userid;
  private String name;
  private boolean isSecure;

  public Authorizer(String userid, Set<String> roles, final String name, boolean isSecure) {
    this.userid = userid;
    this.roles = roles;
    this.name = name;
    this.isSecure = isSecure;
  }

  @Override
  public Principal getUserPrincipal() {
    return new Auth0UserProfile(this.userid, this.name);
  }

  @Override
  public boolean isUserInRole(String role) {
    return roles.contains(role);
  }

  @Override
  public boolean isSecure() {
    return isSecure;
  }

  @Override
  public String getAuthenticationScheme() {
    return SecurityContext.FORM_AUTH;
  }
}
