package io.growingabit.jersey.filters;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import io.growingabit.app.utils.auth.Authorizer;
import io.growingabit.app.utils.ResourceFetcher;
import io.growingabit.app.utils.Settings;
import io.growingabit.jersey.annotations.Secured;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
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

  private static final Configuration config = new Settings(new ResourceFetcher()).getConfig();
  private static final String OAUTH2_SECRET = config.getString("oauth2.secret");
  private static final String OAUTH2_ISSUER = config.getString("oauth2.issuer");

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring("Bearer".length()).trim();
      try {
        DecodedJWT jwt = validateToken(token);

        // I leave here this line if we need user roles in the future
        // Set<String> roles = ImmutableSet.copyOf(jwt.getClaim("roles").asArray(String.class));

        // TODO: WTF??? that's is the user id...
        // I think .split("\\|")[1]; have killed some developer around the world... sorry
        String userid = jwt.getSubject().split("\\|")[1];

        String username = jwt.getClaim("nickname").asString();
        boolean isSecure = requestContext.getSecurityContext().isSecure();

        Authorizer authorizer = new Authorizer(userid, new HashSet<String>(), username, isSecure);
        requestContext.setSecurityContext(authorizer);
      } catch (Exception exception) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      }
    } else {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }

  private DecodedJWT validateToken(String token) throws Exception {
    RSAKeyProvider keyProvider = new RSAKeyProvider() {
      @Override
      public RSAPublicKey getPublicKeyById(String kid) {
        try {
          // Here we can use some more intelligent JwkProvider, that could cache the key.
          // See https://github.com/auth0/jwks-rsa-java
          JwkProvider provider = new UrlJwkProvider("https://growbit-development.eu.auth0.com/");
          Jwk jwk = provider.get(kid);
          RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();
          return (RSAPublicKey) publicKey;
        }catch (Exception e){
          e.printStackTrace();
          return null;
        }
      }

      @Override
      public RSAPrivateKey getPrivateKey() {
        return null;
      }

      @Override
      public String getPrivateKeyId() {
        return null;
      }
    };

    Algorithm algorithm = Algorithm.RSA256(keyProvider);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer(OAUTH2_ISSUER).build();
    return verifier.verify(token);
  }
}
