package io.growingabit.app.utils.auth;

import java.security.Principal;

public class Auth0UserProfile implements Principal {

  private String userid;
  private String name;

  public Auth0UserProfile(String userid, String name) {
    this.userid = userid;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getUserID() {
    return userid;
  }
}
