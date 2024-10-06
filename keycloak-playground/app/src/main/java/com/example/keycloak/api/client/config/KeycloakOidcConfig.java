package com.example.keycloak.api.client.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "keycloak.oidc")
@Data
public class KeycloakOidcConfig {

  @Data
  public static class Endpoints {

    private String authorize;
    private String token;

  }

  private String clientId;
  private String clientSecret;
  private Endpoints endpoints;
  private Map<String, String> redirects = new HashMap<>();

}
