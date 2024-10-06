package com.example.keycloak.api.client.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {

  @Bean
  WebClientCustomizer webClientCustomizer() {
    return webClientBuilder -> {
      HttpClient httpClient = loggingHttpClient();
      webClientBuilder
          .clientConnector(new ReactorClientHttpConnector(httpClient));
    };
  }

  HttpClient loggingHttpClient() {
    return HttpClient.create()
        .wiretap(HttpClient.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
  }
}
