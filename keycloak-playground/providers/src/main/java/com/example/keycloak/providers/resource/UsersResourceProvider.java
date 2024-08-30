package com.example.keycloak.providers.resource;

import com.example.keycloak.providers.resource.model.PutRequiredActionsRequest;
import com.example.keycloak.providers.service.CustomUsersProvider;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Stream;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class UsersResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;
  private final CustomUsersProvider customUsersProvider;
  private final AdminAuthService adminAuthService;

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
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, Map)
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, Map, Integer, Integer)
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, String, Integer, Integer)
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
    adminAuthService.adminPermissionEvaluator().users().requireManage();

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
    adminAuthService.adminPermissionEvaluator().users().requireManage(user);

    request.setRealm(realmValue);
    customUsersProvider.updateUserRequiredActions(user, realm, request);

    return Response.noContent().build();
  }

  @PUT
  @Path("/required-actions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateRequiredActions(@PathParam("realm") String realmValue,
      PutRequiredActionsRequest request) {
    adminAuthService.adminPermissionEvaluator().users().requireManage();

    request.setRealm(realmValue);
    customUsersProvider.updateRequiredActions(request);

    return Response.noContent().build();
  }

}
