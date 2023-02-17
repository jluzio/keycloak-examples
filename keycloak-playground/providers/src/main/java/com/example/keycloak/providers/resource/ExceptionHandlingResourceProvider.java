package com.example.keycloak.providers.resource;

import static java.util.Optional.ofNullable;

import java.lang.reflect.Constructor;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
@JBossLog
public class ExceptionHandlingResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;

  @Override
  public Object getResource() {
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @SuppressWarnings("java:S112")
  public String throwCustom(
      @QueryParam("exception") String exceptionClassName,
      @QueryParam("message") String message
  ) throws Throwable {
    Throwable throwable = throwable(
        ofNullable(exceptionClassName).orElseGet(IllegalArgumentException.class::getName),
        ofNullable(message).orElse("Some exception"));
    log.debugf("Throwing %s", throwable);
    throw throwable;
  }

  @GET
  @Path("/400")
  @Produces(MediaType.APPLICATION_JSON)
  public String throwBadRequestException() {
    throw new javax.ws.rs.BadRequestException("Some exception");
  }

  @GET
  @Path("/401")
  @Produces(MediaType.APPLICATION_JSON)
  public String throwNotAuthorizedException() {
    throw new javax.ws.rs.NotAuthorizedException("Some exception");
  }

  @GET
  @Path("/403")
  @Produces(MediaType.APPLICATION_JSON)
  public String throwForbiddenException() {
    throw new javax.ws.rs.ForbiddenException("Some exception");
  }

  @GET
  @Path("/404")
  @Produces(MediaType.APPLICATION_JSON)
  public String throwNotFoundException() {
    throw new javax.ws.rs.NotFoundException("Some exception");
  }

  @GET
  @Path("/500")
  @Produces(MediaType.APPLICATION_JSON)
  public String throwInternalServerErrorException() {
    throw new javax.ws.rs.InternalServerErrorException("Some exception");
  }

  @Override
  public void close() {
    // not required
  }

  @SuppressWarnings("unchecked")
  private Throwable throwable(String className, String message) {
    try {
      Class<? extends Throwable> clazz = (Class<? extends Throwable>) Class.forName(className);
      Constructor<? extends Throwable> constructor = clazz.getConstructor(String.class);
      return constructor.newInstance(message);
    } catch (Exception e) {
      throw new IllegalArgumentException("Can't create exception", e);
    }
  }

}
