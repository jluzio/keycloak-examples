# Keycloak

## Example start Keycloak with docker
docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:12.0.4

## Docker
- https://hub.docker.com/r/jboss/keycloak/

## Keycloak with Docker and MySQL
- https://github.com/keycloak/keycloak-containers/blob/master/docker-compose-examples/keycloak-mysql.yml

## Test examples
- https://www.baeldung.com/postman-keycloak-endpoints

## Integration tests
- https://github.com/dasniko/testcontainers-keycloak
- https://www.baeldung.com/spring-boot-keycloak-integration-testing
- https://www.youtube.com/watch?v=FEbIW23RoXk

## Clients
Clients to for managing users
- create client in the target realm (app)
- with a Client ID like 'manage-users-client'
- Client authentication enabled
- Service accounts role enabled, and assigned 'manage-users'

Configure realm, client-id, client-secret (generated value).
