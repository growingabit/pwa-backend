package io.growingabit.jersey;

import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.gae.GaeFeature;
import org.glassfish.jersey.servlet.ServletProperties;

import com.google.common.collect.ImmutableMap;

import io.growingabit.app.controllers.MeController;
import io.growingabit.app.controllers.VerificationController;
import io.growingabit.app.model.User;
import io.growingabit.backoffice.controllers.InvitationController;
import io.growingabit.jersey.controllers.HealthCheckController;
import io.growingabit.jersey.filters.CORSFilter;
import io.growingabit.jersey.filters.CharsetFilter;
import io.growingabit.jersey.filters.SecurityFilter;
import io.growingabit.jersey.filters.UserCreationFilter;
import io.growingabit.jersey.providers.GsonProvider;
import io.growingabit.jersey.utils.JerseyContextUserFactory;

public class Application extends ResourceConfig {

  public Application() {
    // Avoid classpath scanning!!
    // Register all statically here

    // Features
    this.register(GsonProvider.class);
    this.register(GaeFeature.class);
    this.register(RolesAllowedDynamicFeature.class);

    // Request filters
    this.register(SecurityFilter.class);
    this.register(UserCreationFilter.class);

    // Response filters
    this.register(CORSFilter.class);
    this.register(CharsetFilter.class);

    // Api endpoints
    this.register(HealthCheckController.class);
    this.register(InvitationController.class);
    this.register(MeController.class);
    this.register(VerificationController.class);

    this.register(new AbstractBinder() {
      @Override
      protected void configure() {
        this.bindFactory(JerseyContextUserFactory.class).to(User.class).in(RequestScoped.class);
      }
    });

    /* @formatter:off */
    final Map<String, Object> params = new ImmutableMap.Builder<String, Object>().put(ServletProperties.FILTER_FORWARD_ON_404, true).build();
    /* @formatter:on */
    this.addProperties(params);
  }
}
