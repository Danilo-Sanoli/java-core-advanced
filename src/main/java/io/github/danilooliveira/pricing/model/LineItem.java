package io.github.danilooliveira.pricing.model;

import java.util.Objects;

public record LineItem(Sku sku, int quantity, Money unitPrice) {
  public LineItem {
    Objects.requireNonNull(sku, "sku");
    Objects.requireNonNull(unitPrice, "unitPrice");
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be > 0");
    }
  }

  public Money gross() {
    return unitPrice.multiply(quantity);
  }
}
