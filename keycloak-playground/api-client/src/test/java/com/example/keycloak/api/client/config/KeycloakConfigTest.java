package com.example.keycloak.api.client.config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KeycloakConfig.class)
@Slf4j
class KeycloakConfigTest {
  @Autowired
  Keycloak keycloak;
  @Autowired
  RealmResource realmResource;

  @Test
  void keycloak() {
    assertThat(keycloak).isNotNull();
  }

  @Test
  void realmResource() {
    assertThat(realmResource).isNotNull();

    realmResource.users().list().forEach(user ->
        log.info("user: {}", ReflectionToStringBuilder.toString(user)));
  }
}