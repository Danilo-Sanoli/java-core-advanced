package io.github.danilooliveira.pricing.stats;

import io.github.danilooliveira.pricing.model.Money;
import java.math.BigDecimal;
import java.util.Objects;

public record OrderStatistics(
    int orders, Money grossTotal, Money discountTotal, Money netTotal, BigDecimal averageTicket) {

  public OrderStatistics {
    Objects.requireNonNull(grossTotal, "grossTotal");
    Objects.requireNonNull(discountTotal, "discountTotal");
    Objects.requireNonNull(netTotal, "netTotal");
    Objects.requireNonNull(averageTicket, "averageTicket");
    if (!grossTotal.currency().equals(discountTotal.currency())
        || !grossTotal.currency().equals(netTotal.currency())) {
      throw new IllegalArgumentException("currency mismatch");
    }
  }
}
