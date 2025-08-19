package io.github.danilooliveira.pricing.model;

import java.util.Objects;

public record Sku(String id) {
  public Sku {
    Objects.requireNonNull(id, "id");
    if (id.isBlank()) {
      throw new IllegalArgumentException("Sku id cannot be null or blank");
    }
  }
}
