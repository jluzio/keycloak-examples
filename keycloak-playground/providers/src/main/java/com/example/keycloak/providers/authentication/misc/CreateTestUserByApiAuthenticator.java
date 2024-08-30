package com.example.keycloak.providers.authentication.misc;

import static java.util.Optional.ofNullable;

import com.example.keycloak.providers.config.ConfigProviders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.idm.UserRepresentation;

@RequiredArgsConstructor
public class CreateTestUserByApiAuthenticator implements Authenticator, AuthenticatorFactory {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private String username;
  private HttpClient httpClient;

  @Override
  public String getId() {
    return "create-test-user-by-api";
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    try {
      UserRepresentation user = new UserRepresentation();
      user.setUsername(username);
      user.setEnabled(true);

      HttpPost request = new HttpPost("http://host.docker.internal:8080/v1/users");
      request.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));
      request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
      request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

      HttpResponse response = httpClient.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode >= 200 && statusCode < 300) {
        if (context.getUser() == null) {
          UserModel userByUsername = context.getSession().users()
              .getUserByUsername(context.getRealm(), username);
          context.setUser(userByUsername);
        }
        context.success();
      } else {
        context.failure(AuthenticationFlowError.INTERNAL_ERROR);
      }
    } catch (Exception e) {
      context.failure(AuthenticationFlowError.INTERNAL_ERROR);
    }
  }

  @Override
  public void action(AuthenticationFlowContext authenticationFlowContext) {
    // not used
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel,
      UserModel userModel) {
    return false;
  }

  @Override
  public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel,
      UserModel userModel) {
    // not used
  }

  @Override
  public void close() {
    // not used
  }

  @Override
  public void init(Scope config) {
    Scope scopedConfig = ConfigProviders.environment().scope("authentication.createTestUser");
    username = ofNullable(scopedConfig.get("username"))
        .orElse("testuser");
    httpClient = HttpClients.custom().build();
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
    // not used
  }

  @Override
  public Authenticator create(KeycloakSession session) {
    return this;
  }

  @Override
  public String getReferenceCategory() {
    return null;
  }

  @Override
  public boolean isConfigurable() {
    return false;
  }

  @Override
  public Requirement[] getRequirementChoices() {
    return Requirement.values();
  }

  @Override
  public boolean isUserSetupAllowed() {
    return true;
  }

  @Override
  public String getDisplayType() {
    return "Create Test User by Api";
  }

  @Override
  public String getHelpText() {
    return getDisplayType();
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return List.of();
  }

}
