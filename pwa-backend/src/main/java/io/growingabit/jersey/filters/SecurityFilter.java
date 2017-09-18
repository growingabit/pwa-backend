package io.growingabit.jersey.filters;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.common.collect.ImmutableSet;
import io.growingabit.app.utils.Settings;
import io.growingabit.app.utils.auth.Authorizer;
import io.growingabit.jersey.annotations.Secured;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

@Secured
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

  private static final Configuration config = Settings.getConfig();
  private static final String OAUTH2_ISSUER = config.getString("oauth2.issuer");
  private static final String CUSTOM_CLAIMS_NAMESPACE = "https://growbit.io/";
  private static final String ROLES_CLAIM = CUSTOM_CLAIMS_NAMESPACE + "roles";
  private static final String JWK_URL = config.getString("jwk.url");

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {

    final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      final String token = authorizationHeader.substring("Bearer".length()).trim();
      try {
        final DecodedJWT jwt = validateToken(token);

        final String[] roles = jwt.getClaim(ROLES_CLAIM).asArray(String.class);
        Set<String> rolesSet = null;
        if (ArrayUtils.isNotEmpty(roles)) {
          rolesSet = ImmutableSet.copyOf(roles);
        } else {
          rolesSet = new HashSet<>();
        }

        final String userid = jwt.getSubject();

        final String username = jwt.getClaim("nickname").asString();
        final boolean isSecure = requestContext.getSecurityContext().isSecure();

        final Authorizer authorizer = new Authorizer(userid, rolesSet, username, isSecure);
        requestContext.setSecurityContext(authorizer);
      } catch (final Exception exception) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      }
    } else {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }

  private DecodedJWT validateToken(final String token) throws Exception {
    final RSAKeyProvider keyProvider = new KeyProvider(JWK_URL);
    final Algorithm algorithm = Algorithm.RSA256(keyProvider);
    final JWTVerifier verifier = JWT.require(algorithm).withIssuer(OAUTH2_ISSUER).build();
    return verifier.verify(token);
  }

  private class KeyProvider implements RSAKeyProvider {

    private final XLogger logger = XLoggerFactory.getXLogger(KeyProvider.class);
    private final JwkProvider jwkProvider;

    // Here we can use some more intelligent JwkProvider, that could cache the key.
    // See https://github.com/auth0/jwks-rsa-java
    public KeyProvider(final String url) {
      this.jwkProvider = new UrlJwkProvider(url);
    }

    @Override
    public RSAPublicKey getPublicKeyById(final String kid) {
      try {
        final Jwk jwk = this.jwkProvider.get(kid);
        final RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();
        return (RSAPublicKey) publicKey;
      } catch (final Exception e) {

        this.logger.error("Error retrieving Auth0 JWK file", e);
        return null;
      }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
      // we can safely return null, beacuse we only isValid tokens
      return null;
    }

    @Override
    public String getPrivateKeyId() {
      // we can safely return null, beacuse we only isValid tokens
      return null;
    }
  }
}
