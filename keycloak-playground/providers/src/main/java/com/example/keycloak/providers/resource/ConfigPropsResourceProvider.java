package com.example.keycloak.providers.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class ConfigPropsResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;
  private final Map<String, Object> values;

  @Override
  public Object getResource() {
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getValues() {
    return values;
  }

  @Override
  public void close() {
    // not required
  }

}
