package com.example.keycloak.providers.resource;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@JBossLog
public class GreetingResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;

  @Override
  public Object getResource() {
    return this;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get(@PathParam("realm") String realmValue) {
    var realm = session.getContext().getRealm();
    log.infof("realm by context :: %s", realm);
    return "Hello " + realmValue;
  }

  @Override
  public void close() {
    // not required
  }

}
