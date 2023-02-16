package com.example.keycloak.providers.resource;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

@RequiredArgsConstructor
@Data
@JBossLog
public class AdminAuthService {
  private final KeycloakSession session;

  public AdminPermissionEvaluator adminPermissionEvaluator() {
    AdminAuth adminAuth = authenticateRealmAdminRequest();
    return AdminPermissions.evaluator(
        session,
        session.getContext().getRealm(),
        adminAuth);
  }

  /**
   * @see org.keycloak.services.resources.admin.AdminRoot#authenticateRealmAdminRequest(HttpHeaders)
   */
  public AdminAuth authenticateRealmAdminRequest() {
    var headers = session.getContext().getRequestHeaders();
    var connection = session.getContext().getConnection();

    String tokenString = AppAuthManager.extractAuthorizationHeaderToken(headers);
    if (tokenString == null) {
      throw new NotAuthorizedException("Bearer");
    }
    AccessToken token;
    try {
      JWSInput input = new JWSInput(tokenString);
      token = input.readJsonContent(AccessToken.class);
    } catch (JWSInputException e) {
      throw new NotAuthorizedException("Bearer token format error");
    }
    String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
    RealmManager realmManager = new RealmManager(session);
    RealmModel realm = realmManager.getRealmByName(realmName);
    if (realm == null) {
      throw new NotAuthorizedException("Unknown realm in token");
    }
    session.getContext().setRealm(realm);

    AuthenticationManager.AuthResult authResult =
        new AppAuthManager.BearerTokenAuthenticator(session)
            .setRealm(realm)
            .setConnection(connection)
            .setHeaders(headers)
            .authenticate();

    if (authResult == null) {
      log.debug("Token not valid");
      throw new NotAuthorizedException("Bearer");
    }

    return new AdminAuth(realm, authResult.getToken(), authResult.getUser(),
        authResult.getClient());
  }

}
