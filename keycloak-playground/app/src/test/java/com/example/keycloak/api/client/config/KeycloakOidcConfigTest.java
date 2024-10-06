package com.example.keycloak.api.client.config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Log4j2
class KeycloakOidcConfigTest {

  @Configuration
  @EnableConfigurationProperties(KeycloakOidcConfig.class)
  static class Config {

  }

  @Autowired
  KeycloakOidcConfig config;

  @Test
  void validate() {
    log.debug(config);
    assertThat(config)
        .hasNoNullFieldsOrProperties();
  }

}