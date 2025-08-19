package io.github.danilooliveira.pricing.promo;

import io.github.danilooliveira.pricing.model.Sku;
import java.util.Objects;

public record BuyXGetY(Sku skuX, int qtyX, Sku skuY, int qtyY) implements Promotion {
  public BuyXGetY {
    Objects.requireNonNull(skuX, "skuX");
    Objects.requireNonNull(skuY, "skuY");
    if (qtyX <= 0 || qtyY <= 0) {
      throw new IllegalArgumentException("qtyX and qtyY must be > 0");
    }
  }
}
