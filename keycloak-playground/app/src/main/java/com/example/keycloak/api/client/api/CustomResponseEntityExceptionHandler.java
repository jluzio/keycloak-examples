package com.example.keycloak.api.client.api;

import java.util.Map;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({WebClientResponseException.class})
  public final Mono<ResponseEntity<Object>> handleResponseException(WebClientResponseException ex,
      ServerWebExchange exchange) {
    ProblemDetail body = createProblemDetail(
        ex,
        ex.getStatusCode(),
        ex.getStatusText(),
        null,
        null,
        exchange
    );
    body.setProperties(ex.getResponseBodyAs(Map.class));

    return handleExceptionInternal(ex, body, ex.getHeaders(), ex.getStatusCode(), exchange);
  }

}
