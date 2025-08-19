package io.github.danilooliveira.pricing.promo;

import java.math.BigDecimal;
import java.util.Objects;

public record PercentageOff(BigDecimal percent) implements Promotion {
  public PercentageOff {
    Objects.requireNonNull(percent, "percent");
    if (percent.compareTo(BigDecimal.ZERO) < 0 || percent.compareTo(BigDecimal.ONE) > 0)
      throw new IllegalArgumentException("percent must be between [0,1]");
  }
}
