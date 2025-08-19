package io.github.danilooliveira.pricing.promo;

import io.github.danilooliveira.pricing.model.Money;
import java.util.Objects;

public record FixedAmountOff(Money amount) implements Promotion {
  public FixedAmountOff {
    Objects.requireNonNull(amount, "amount");
    if (amount.amount().signum() <= 0) {
      throw new IllegalArgumentException("amount must be > 0");
    }
  }
}
