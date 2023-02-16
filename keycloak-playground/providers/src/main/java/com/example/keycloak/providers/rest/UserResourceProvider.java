package com.example.keycloak.providers.rest;

import com.example.keycloak.providers.rest.model.PutRequiredActionsRequest;
import com.example.keycloak.providers.service.CustomUsersProvider;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.ClientConnection;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

@RequiredArgsConstructor
@Data
@JBossLog
public class UserResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;
  private final CustomUsersProvider customUsersProvider;

  @Context
  protected ClientConnection clientConnection;
  @Context
  private HttpHeaders httpHeaders;
  @Context
  protected HttpRequest request;

  @Override
  public Object getResource() {
    ResteasyProviderFactory.getInstance().injectProperties(this);
    return this;
  }

  @Override
  public void close() {
    // not required
  }

  /**
   * @see org.keycloak.models.UserProvider#getUsersStream(RealmModel)
   * @see org.keycloak.models.UserProvider#getUsersStream(RealmModel, boolean)
   * @see org.keycloak.models.UserProvider#getUsersStream(RealmModel, Integer, Integer, boolean)
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Stream<UserRepresentation> getUsers(@PathParam("realm") String realmValue) {
    var realm = session.getContext().getRealm();
    return session.users()
        .searchForUserStream(realm, Map.of(
            UserModel.INCLUDE_SERVICE_ACCOUNT, Boolean.FALSE.toString()
        ))
        .map(user -> {
          var representation = new UserRepresentation();
          representation.setId(user.getId());
          representation.setUsername(user.getUsername());
          return representation;
        });
  }

  @PUT
  @Path("/{id}/touch")
  @Produces(MediaType.APPLICATION_JSON)
  public Response touchUser(@PathParam("realm") String realmValue, @PathParam("id") String id) {
    authenticateRealmAdmin().users().requireManage();

    var realm = session.getContext().getRealm();
    var user = session.users().getUserById(realm, id);
    user.setSingleAttribute("touchedAt", OffsetDateTime.now().toString());
    return Response.noContent().build();
  }

  @PUT
  @Path("/{id}/required-actions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateRequiredActions(@PathParam("realm") String realmValue,
      @PathParam("id") String id, PutRequiredActionsRequest request) {
    var realm = session.getContext().getRealm();
    var user = session.users().getUserById(realm, id);
    authenticateRealmAdmin().users().requireManage(user);

    request.setRealm(realmValue);
    customUsersProvider.updateUserRequiredActions(user, realm, request);

    return Response.noContent().build();
  }

  @PUT
  @Path("/required-actions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateRequiredActions(@PathParam("realm") String realmValue,
      PutRequiredActionsRequest request) {
    authenticateRealmAdmin().users().requireManage();

    request.setRealm(realmValue);
    customUsersProvider.updateRequiredActions(request);

    return Response.noContent().build();
  }

  private AdminPermissionEvaluator authenticateRealmAdmin() {
    var adminAuth = authenticateRealmAdminRequest(httpHeaders);
    return AdminPermissions.evaluator(
        session,
        session.getContext().getRealm(),
        adminAuth);
  }

  /**
   * @see org.keycloak.services.resources.admin.AdminRoot#authenticateRealmAdminRequest(HttpHeaders)
   */
  protected AdminAuth authenticateRealmAdminRequest(HttpHeaders headers) {
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

    AuthenticationManager.AuthResult authResult = new AppAuthManager.BearerTokenAuthenticator(
        session)
        .setRealm(realm)
        .setConnection(clientConnection)
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
