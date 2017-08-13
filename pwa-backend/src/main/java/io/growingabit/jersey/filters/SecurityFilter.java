package io.growingabit.jersey.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableSet;
import io.growingabit.app.utils.Authorizer;
import io.growingabit.app.utils.Settings;
import io.growingabit.jersey.annotations.Secured;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.commons.configuration2.Configuration;

@Secured
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

  private static final Configuration config = Settings.getConfiguration();
  private static final String OAUTH2_SECRET = config.getString("oauth2.secret");
  private static final String OAUTH2_ISSUER = config.getString("oauth2.issuer");

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring("Bearer".length()).trim();
      try {
        DecodedJWT jwt = validateToken(token);
        Set<String> roles = ImmutableSet.copyOf(jwt.getClaim("roles").asArray(String.class));
        String username = jwt.getClaim("nickname").asString();
        boolean isSecure = requestContext.getSecurityContext().isSecure();
        Authorizer authorizer = new Authorizer(roles, username, isSecure);
        requestContext.setSecurityContext(authorizer);
      } catch (Exception exception) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      }
    } else {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }

  private DecodedJWT validateToken(String token) throws Exception {
    Algorithm algorithm = Algorithm.HMAC256(OAUTH2_SECRET);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer(OAUTH2_ISSUER).build(); //Reusable verifier instance
    return verifier.verify(token);
  }
}
