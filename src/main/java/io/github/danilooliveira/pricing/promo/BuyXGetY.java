package io.github.danilooliveira.pricing.promo;

import io.github.danilooliveira.pricing.model.Sku;

public record BuyXGetY(Sku skuX, int qtyX, Sku skuY, int qtyY) implements Promotion {}
