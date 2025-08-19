package io.github.danilooliveira.pricing.model;

import java.util.List;
import java.util.Objects;

public record Order(String id, List<LineItem> items, String coupon) {
  public Order {
    Objects.requireNonNull(items);
    items = List.copyOf(items);
  }
}
