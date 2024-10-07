package com.example.keycloak.providers.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class OidcResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;

  @Override
  public Object getResource() {
    return this;
  }

  @Override
  public void close() {
    // not required
  }

  @GET
  @Path("/callback")
  public String callback(
      @QueryParam("code") String code,
      @QueryParam("session_state") String sessionState,
      @QueryParam("iss") String issuer
  ) {
    log.debugf("callback: %s | %s | %s", code, sessionState, issuer);
    return code;
  }

}
