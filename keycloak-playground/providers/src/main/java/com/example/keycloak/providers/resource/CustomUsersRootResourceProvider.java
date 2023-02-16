package com.example.keycloak.providers.resource;

import com.example.keycloak.providers.service.CustomUsersProvider;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class CustomUsersRootResourceProvider implements RealmResourceProvider {

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

  @Path("{id}")
  public UserResource userResource(@PathParam("id") String id) {
    var realm = session.getContext().getRealm();
    var user = session.users().getUserById(realm, id);
    var userResource = new UserResource(session, realm, user);
    ResteasyProviderFactory.getInstance().injectProperties(userResource);
    return userResource;
  }

  @Path("")
  public UsersResource usersResource() {
    var realm = session.getContext().getRealm();
    var usersResource = new UsersResource(session, realm);
    ResteasyProviderFactory.getInstance().injectProperties(usersResource);
    return usersResource;
  }

}
