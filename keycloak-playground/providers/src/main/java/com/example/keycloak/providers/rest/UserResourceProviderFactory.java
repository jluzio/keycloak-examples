package com.example.keycloak.providers.rest;

import com.example.keycloak.providers.service.CustomUsersProvider;
import lombok.RequiredArgsConstructor;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@RequiredArgsConstructor
public class UserResourceProviderFactory implements RealmResourceProviderFactory {

  public static final String ID = "users";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public RealmResourceProvider create(KeycloakSession session) {
    var customUsersProvider = new CustomUsersProvider(session);
    return new UserResourceProvider(session, customUsersProvider);
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
