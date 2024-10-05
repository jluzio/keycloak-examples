package com.example.keycloak.providers.config;

import org.keycloak.Config.ConfigProvider;
import org.keycloak.Config.Scope;

public class EnvironmentConfigProvider implements ConfigProvider {
  public static final String NS_KEYCLOAK = "kc";
  public static final String NS_KEYCLOAK_PREFIX = NS_KEYCLOAK + ".";
  public static final String OPTION_PART_SEPARATOR = ".";

  @Override
  public String getProvider(String spi) {
    return System.getenv(NS_KEYCLOAK + spi + ".provider");
  }

  @Override
  public String getDefaultProvider(String spi) {
    // Should be fine, but was unable to confirm the usage
    return getProvider(spi);
  }

  @Override
  public Scope scope(String... scope) {
    StringBuilder sb = new StringBuilder();
    sb.append(NS_KEYCLOAK_PREFIX);
    for (String s : scope) {
      sb.append(s);
      sb.append(OPTION_PART_SEPARATOR);
    }
    return new EnvironmentScope(sb.toString());
  }
}
