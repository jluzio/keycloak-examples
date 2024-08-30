package com.example.keycloak.api.client;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Slf4j
class KeycloakExampleIT {

  @Container
  private KeycloakContainer container = new KeycloakContainer("quay.io/keycloak/keycloak:24.0.2")
      .withRealmImportFile("/keycloak/app-realm-export.json");

  @Test
  void test() {
    Keycloak keycloakAdmin = container.getKeycloakAdminClient();
    log.debug("--- realms ---");
    keycloakAdmin.realms().findAll().forEach(realm -> {
      log.info("realm: {}", realm.getRealm());
    });
  }

}
