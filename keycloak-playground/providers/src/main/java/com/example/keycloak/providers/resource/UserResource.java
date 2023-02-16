package com.example.keycloak.providers.resource;

import java.time.OffsetDateTime;
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
  @Context
  private HttpHeaders httpHeaders;
  @Context
  protected HttpRequest request;

  @PUT
  @Path("touch")
  @Produces(MediaType.APPLICATION_JSON)
  public Response touchUser() {
    user.setSingleAttribute("touchedAt", OffsetDateTime.now().toString());
    return Response.noContent().build();
  }

}
