package com.example.keycloak.providers.resource;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class ConfigPropsResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;
  private final Map<String, Object> values;

  @Context
  protected ClientConnection clientConnection;
  @Context
  private HttpHeaders httpHeaders;
  @Context
  protected HttpRequest request;

  @Override
  public Object getResource() {
    ResteasyProviderFactory.getInstance().injectProperties(this);
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
