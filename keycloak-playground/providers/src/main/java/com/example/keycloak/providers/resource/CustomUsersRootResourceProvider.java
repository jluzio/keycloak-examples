package com.example.keycloak.providers.resource;

import com.example.keycloak.providers.service.CustomUsersProvider;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@Data
@JBossLog
public class CustomUsersRootResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;
  private final CustomUsersProvider customUsersProvider;
  private final AdminAuthService adminAuthService;

  @Override
  public Object getResource() {
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
    return new UserResource(session, realm, user);
  }

  @Path("")
  public UsersResource usersResource() {
    var realm = session.getContext().getRealm();
    return new UsersResource(session, realm);
  }

}
