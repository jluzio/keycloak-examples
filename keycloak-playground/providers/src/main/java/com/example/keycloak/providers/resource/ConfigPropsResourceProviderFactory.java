package com.example.keycloak.providers.resource;

import com.example.keycloak.providers.config.ConfigProviders;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@RequiredArgsConstructor
@JBossLog
public class ConfigPropsResourceProviderFactory implements RealmResourceProviderFactory {

  public static final String ID = "config-props";
  private Map<String, Object> values = Map.of();

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public RealmResourceProvider create(KeycloakSession session) {
    return new ConfigPropsResourceProvider(session, values);
  }

  @Override
  public void init(Scope config) {
    var scopedConfig = ConfigProviders.environment().scope("config.props");
    values = new HashMap<>();
    Stream.of("username", "password")
        .forEach(k -> {
          values.put(k, scopedConfig.get(k));
        });
    log.debugf("values: %s", values);
    log.debugf("done!");
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
    // not required
  }

  @Override
  public void close() {
    // not required
  }

}
