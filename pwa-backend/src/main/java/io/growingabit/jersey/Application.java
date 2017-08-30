package io.growingabit.jersey;

import java.util.Map;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.gae.GaeFeature;
import org.glassfish.jersey.servlet.ServletProperties;

import com.google.common.collect.ImmutableMap;

import io.growingabit.backoffice.controllers.InvitationController;
import io.growingabit.jersey.controllers.HealthCheckController;
import io.growingabit.jersey.filters.SecurityFilter;
import io.growingabit.jersey.providers.GsonProvider;

public class Application extends ResourceConfig {

    public Application() {
        this.register(GsonProvider.class);
        this.register(GaeFeature.class);
        this.register(RolesAllowedDynamicFeature.class);

        this.register(SecurityFilter.class);

        // Avoid classpath scanning!!
        // Register all endpoints class here
        this.register(HealthCheckController.class);
        this.register(InvitationController.class);

    /* @formatter:off */
    Map<String, Object> params = new ImmutableMap.Builder<String, Object>()
        .put(ServletProperties.FILTER_FORWARD_ON_404, true)
        .build();
    /* @formatter:on */
        this.addProperties(params);
    }
}
