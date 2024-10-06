package com.example.keycloak.api.client;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestSupport {

  public static final String LIVE_TEST = "#{systemEnvironment['LIVE_TEST'] == 'true'}";

}
