package io.github.danilooliveira.pricing.promo;

import io.github.danilooliveira.pricing.model.Money;

public record FixedAmountOff(Money amount) implements Promotion {}
