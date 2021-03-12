package com.example.keycloak.api.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.api")
@Data
public class KeycloakApiConfig {

  private String url;
  private String clientId;
  private String clientSecret;
  private String grantType;
  private String realm;

}
