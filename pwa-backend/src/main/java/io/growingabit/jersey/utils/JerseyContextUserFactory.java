package io.growingabit.jersey.utils;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

import org.glassfish.hk2.api.Factory;

import io.growingabit.app.model.User;

public class JerseyContextUserFactory implements Factory<User> {

  public static final String CONTEXT_USER_PROPERTY_NAME = "currentUser";

  private final ContainerRequestContext context;

  @Inject
  public JerseyContextUserFactory(final ContainerRequestContext context) {
    this.context = context;
  }

  @Override
  public User provide() {
    return (User) this.context.getProperty(CONTEXT_USER_PROPERTY_NAME);
  }

  @Override
  public void dispose(final User t) {
    // nothing to do
  }
}
