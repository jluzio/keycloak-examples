package com.example.keycloak.api.client.api;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

  private final Keycloak keycloak;
  private final RealmResource realmResource;

  @GetMapping
  public List<UserRepresentation> searchUsers(@RequestParam String username) {
    return realmResource.users().search(username);
  }

  @GetMapping("/{id}")
  public UserRepresentation getUser(@PathVariable String id) {
    return realmResource.users().get(id).toRepresentation();
  }

  @PostMapping
  public ResponseEntity<Object> createUser(UserRepresentation user) {
    try (Response response = realmResource.users().create(user)) {
      return response.getStatusInfo().getFamily() == Family.SUCCESSFUL
          ? ResponseEntity.noContent().build()
          : ResponseEntity.status(response.getStatus()).body(response.getEntity());
    }
  }

}
