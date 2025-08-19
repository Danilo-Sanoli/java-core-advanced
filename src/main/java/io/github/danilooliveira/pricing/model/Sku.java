package io.github.danilooliveira.pricing.model;

public record Sku(String id) {

  public Sku {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Sku id cannot be null or blank");
    }
  }
}
