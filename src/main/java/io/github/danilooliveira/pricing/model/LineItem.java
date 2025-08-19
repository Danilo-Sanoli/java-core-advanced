package io.github.danilooliveira.pricing.model;

import java.math.BigDecimal;

public record LineItem(Sku sku, int quantity, Money unitPrice) {
  public LineItem {
    if (quantity <= 0) throw new IllegalArgumentException("the quantity must be greater than 0");
  }

  public Money gross() {
    return new Money(
        unitPrice.amount().multiply(BigDecimal.valueOf(quantity)), unitPrice.currency());
  }
}
