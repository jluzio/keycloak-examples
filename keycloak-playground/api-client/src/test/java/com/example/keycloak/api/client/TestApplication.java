package com.example.keycloak.api.client;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class TestApplication {

  @TestConfiguration(proxyBeanMethods = false)
  static class LocalDevTestcontainersConfig {

    @Bean
//    @ServiceConnection
    @RestartScope
    public KeycloakContainer keycloakContainer() {
      return new KeycloakContainer("quay.io/keycloak/keycloak:24.0.2")
          .withRealmImportFile("/keycloak/app-realm-export.json")
          .withExposedPorts(8180);
    }
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(LocalDevTestcontainersConfig.class)
        .run(args);
  }
}
