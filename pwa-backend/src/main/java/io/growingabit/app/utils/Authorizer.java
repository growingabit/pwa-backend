package io.growingabit.app.utils;

import java.security.Principal;
import java.util.Set;
import javax.ws.rs.core.SecurityContext;

public class Authorizer implements SecurityContext {

  private Set<String> roles;
  private String username;
  private boolean isSecure;

  public Authorizer(Set<String> roles, final String username, boolean isSecure) {
    this.roles = roles;
    this.username = username;
    this.isSecure = isSecure;
  }

  @Override
  public Principal getUserPrincipal() {
    return new Principal() {
      @Override
      public String getName() {
        return username;
      }
    };
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