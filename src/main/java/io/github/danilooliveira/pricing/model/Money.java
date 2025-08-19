package io.github.danilooliveira.pricing.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
  public Money {
    Objects.requireNonNull(amount);
    Objects.requireNonNull(currency);
  }

  public Money add(Money other) {
    requireSameCurrency(other);
    return new Money(amount.add(other.amount), currency);
  }

  public Money negate() {
    return new Money(amount.negate(), currency);
  }

  private void requireSameCurrency(Money other) {
    if (!currency.equals(other.currency)) throw new IllegalArgumentException("Different currency");
  }
}
