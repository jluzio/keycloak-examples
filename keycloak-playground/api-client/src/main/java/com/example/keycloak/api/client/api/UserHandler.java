package com.example.keycloak.api.client.api;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

  private final Keycloak keycloak;
  private final RealmResource realmResource;


  public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
    return Mono.just(serverRequest.pathVariable("id"))
        .flatMap(id -> Mono.just(realmResource.users().get(id).toRepresentation()))
        .flatMap(user -> ServerResponse.ok().bodyValue(user))
        .onErrorResume(NotFoundException.class, e -> ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> search(ServerRequest serverRequest) {
    return Mono.justOrEmpty(serverRequest.queryParam("username"))
        .map(username -> realmResource.users().search(username))
        .flatMap(users -> ServerResponse.ok().bodyValue(users));
  }

  public Mono<ServerResponse> postUser(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(UserRepresentation.class)
        .flatMap(user -> {
          try (Response response = realmResource.users().create(user)) {
            boolean success = response.getStatusInfo().getFamily() == Family.SUCCESSFUL;
            return success
                ? ServerResponse.noContent().build()
                : ServerResponse.status(response.getStatus()).build();
          }
        });
  }

}
