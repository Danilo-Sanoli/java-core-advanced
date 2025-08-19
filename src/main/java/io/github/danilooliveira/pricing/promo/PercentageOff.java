package io.github.danilooliveira.pricing.promo;

import java.math.BigDecimal;

public record PercentageOff(BigDecimal percent) implements Promotion {
  public PercentageOff {
    if (percent.compareTo(BigDecimal.ZERO) < 0 || percent.compareTo(new BigDecimal("1.00")) > 0)
      throw new IllegalArgumentException("percent must be between [0,1]");
  }
}
