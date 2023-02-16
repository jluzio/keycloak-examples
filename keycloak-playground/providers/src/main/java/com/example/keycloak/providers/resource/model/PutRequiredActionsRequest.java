package com.example.keycloak.providers.resource.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PutRequiredActionsRequest {

  private List<String> actions;
  private Mode mode;
  private String realm;
  private Integer pageSize;
  private boolean cacheEvict;

  public enum Mode {
    ADD, REMOVE, REPLACE
  }

}
