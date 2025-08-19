package io.github.danilooliveira.pricing.promo;

public sealed interface Promotion permits PercentageOff, FixedAmountOff, BuyXGetY {}
