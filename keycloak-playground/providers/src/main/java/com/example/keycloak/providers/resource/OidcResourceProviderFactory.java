package com.example.keycloak.providers.resource;

import lombok.RequiredArgsConstructor;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@RequiredArgsConstructor
public class OidcResourceProviderFactory implements RealmResourceProviderFactory {

  public static final String ID = "oidc";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public RealmResourceProvider create(KeycloakSession session) {
    return new OidcResourceProvider(session);
  }

  @Override
  public void init(Scope config) {
    // not required
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
