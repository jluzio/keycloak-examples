package com.example.keycloak.api.client;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

public class TestApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    public KeycloakContainer keycloakContainer() {
      return new KeycloakContainer("quay.io/keycloak/keycloak:25.0")
          .withExposedPorts(8180)
          .withRealmImportFile("/keycloak/app-realm-export-base.json")
          ;
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(LocalDevTestcontainersConfig.class)
        .run(args);
  }
}
