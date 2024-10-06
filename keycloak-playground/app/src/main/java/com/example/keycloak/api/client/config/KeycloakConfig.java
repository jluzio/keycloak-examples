package com.example.keycloak.api.client.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(KeycloakApiConfig.class)
@RequiredArgsConstructor
//@ConditionalOnProperty("keycloak.api")
public class KeycloakConfig {

  private final KeycloakApiConfig properties;


  @Bean
  public Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(properties.getServerUrl())
        .realm(properties.getRealm())
        .username(properties.getUsername())
        .password(properties.getPassword())
        .clientId(properties.getClientId())
        .clientSecret(properties.getClientSecret())
        .grantType(properties.getGrantType())
        .build();
  }

  @Bean
  public RealmResource realmResource(Keycloak keycloak) {
    return keycloak.realm(properties.getRealm());
  }

  @Bean
  WebClient keycloakWebClient(WebClient.Builder builder) {
    return builder.build();
  }

}