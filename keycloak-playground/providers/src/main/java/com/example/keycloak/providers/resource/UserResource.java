package com.example.keycloak.providers.resource;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@RequiredArgsConstructor
@Data
@JBossLog
public class UserResource {

  private final KeycloakSession session;
  private final RealmModel realm;
  private final UserModel user;

  @PUT
  @Path("touch")
  @Produces(MediaType.APPLICATION_JSON)
  public Response touchUser() {
    user.setSingleAttribute("touchedAt", OffsetDateTime.now().toString());
    return Response.noContent().build();
  }

}
