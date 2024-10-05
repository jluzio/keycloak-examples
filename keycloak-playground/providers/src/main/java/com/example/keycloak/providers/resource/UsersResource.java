package com.example.keycloak.providers.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.account.UserRepresentation;

@RequiredArgsConstructor
@Data
@JBossLog
public class UsersResource {

  private final KeycloakSession session;
  private final RealmModel realm;

  /**
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, Map)
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, Map, Integer, Integer)
   * @see org.keycloak.models.UserProvider#searchForUserStream(RealmModel, String, Integer, Integer)
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Stream<UserRepresentation> getUsers() {
    return session.users()
        .searchForUserStream(realm, Map.of())
        .map(user -> {
          var representation = new UserRepresentation();
          representation.setId(user.getId());
          representation.setUsername(user.getUsername());
          return representation;
        });
  }

}
