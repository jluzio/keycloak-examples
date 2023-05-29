package com.example.keycloak.providers.service;

import com.example.keycloak.providers.resource.model.PutRequiredActionsRequest;
import com.example.keycloak.providers.resource.model.PutRequiredActionsRequest.Mode;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
//import org.keycloak.models.cache.UserCache;

@RequiredArgsConstructor
@Data
@JBossLog
public class CustomUsersProvider {

  private final KeycloakSession session;


  public void updateRequiredActions(PutRequiredActionsRequest request) {
    var realm = session.realms().getRealm(request.getRealm());
    var pageSize = request.getPageSize();
    if (pageSize == null) {
      session.users().getUsersStream(realm, false)
          .forEach(user -> updateUserRequiredActions(user, realm, request));
    } else {
      var userCount = session.users().getUsersCount(realm, false);
      for (var from = 0; from < userCount; from += pageSize) {
        session.users().getUsersStream(realm, from, pageSize, false)
            .forEach(user -> updateUserRequiredActions(user, realm, request));
      }
    }
  }

  public void updateUserRequiredActions(
      UserModel user, RealmModel realm, PutRequiredActionsRequest request) {
    if (request.getMode() == Mode.ADD) {
      request.getActions().forEach(user::addRequiredAction);
    } else if (request.getMode() == Mode.REMOVE) {
      request.getActions().forEach(user::removeRequiredAction);
    } else if (request.getMode() == Mode.REPLACE) {
      var currentActions = user.getRequiredActionsStream().collect(Collectors.toList());
      currentActions.forEach(user::removeRequiredAction);
      request.getActions().forEach(user::addRequiredAction);
    }
    // requires org.keycloak:keycloak-model-infinispan
//    if (request.isCacheEvict()) {
//      evictUserFromCacheIfPossible(user, realm);
//    }
  }

//  private Optional<UserCache> userCache() {
//    return Optional.of(session.users())
//        .filter(UserCache.class::isInstance)
//        .map(UserCache.class::cast);
//  }
//
//  private void evictUserFromCacheIfPossible(UserModel user, RealmModel realm) {
//    userCache().ifPresent(cache -> cache.evict(realm, user));
//  }

}
