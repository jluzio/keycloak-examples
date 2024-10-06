package com.example.keycloak.api.client.api;

import com.example.keycloak.api.client.config.KeycloakOidcConfig;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oidc")
@RequiredArgsConstructor
@Slf4j
public class OidcController {

  private final KeycloakOidcConfig config;
  @Qualifier("keycloakWebClient")
  private final WebClient webClient;

  @GetMapping("/authorize")
  public ResponseEntity<Void> authorize(
      @RequestParam String username,
      @RequestParam(required = false) String redirect) {
    var redirectUriDefault = config.getRedirects().get("default");
    String redirectUri = Optional.ofNullable(redirect)
        .map(config.getRedirects()::get)
        .orElse(redirectUriDefault);
    var authorizeUri = UriComponentsBuilder.fromHttpUrl(config.getEndpoints().getAuthorize())
        .queryParam("client_id", config.getClientId())
        .queryParam("client_secret", config.getClientSecret())
        .queryParam("username", username)
        .queryParam("scope", "openid")
        .queryParam("response_type", "code")
        .queryParam("redirect_uri", redirectUri)
        .build()
        .toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(authorizeUri);
    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }

  @GetMapping("/callback-log")
  public String callbackLog(
      @RequestParam String code,
      @RequestParam(value = "session_state", required = false) String sessionState,
      @RequestParam(value = "iss", required = false) String issuer
  ) {
    log.debug("callback: {} | {} | {}", code, sessionState, issuer);
    return code;
  }

  @GetMapping("/callback-token")
  public Mono<Map<String, String>> callbackToken(
      @RequestParam String code,
      @RequestParam(value = "session_state", required = false) String sessionState,
      @RequestParam(value = "iss", required = false) String issuer
  ) {
    log.debug("callback: {} | {} | {}", code, sessionState, issuer);
    var redirectUri = config.getRedirects().get("token");

    var requestBody = new LinkedMultiValueMap<String, String>();
    requestBody.set("client_id", config.getClientId());
    requestBody.set("client_secret", config.getClientSecret());
    requestBody.set("grant_type", "authorization_code");
    requestBody.set("code", code);
    requestBody.set("scope", "openid");
    requestBody.set("redirect_uri", redirectUri);

    return webClient.post()
        .uri(config.getEndpoints().getToken())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(requestBody))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
        .log();
  }

}
