package io.github.danilooliveira.pricing.promo;

import static org.junit.jupiter.api.Assertions.*;

import io.github.danilooliveira.pricing.model.Money;
import io.github.danilooliveira.pricing.model.Sku;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PromotionValidationTest {

  @Test
  void percentageBounds() {
    assertThrows(IllegalArgumentException.class, () -> new PercentageOff(new BigDecimal("-0.01")));
    assertThrows(IllegalArgumentException.class, () -> new PercentageOff(new BigDecimal("1.01")));
    assertDoesNotThrow(() -> new PercentageOff(new BigDecimal("0.00")));
    assertDoesNotThrow(() -> new PercentageOff(new BigDecimal("1.00")));
  }

  @Test
  void fixedAmountMustBePositive() {
    assertThrows(IllegalArgumentException.class, () -> new FixedAmountOff(Money.of(0.0, "BRL")));
    assertThrows(IllegalArgumentException.class, () -> new FixedAmountOff(Money.of(-1.0, "BRL")));
    assertDoesNotThrow(() -> new FixedAmountOff(Money.of(1.0, "BRL")));
  }

  @Test
  void bxgyQuantitiesMustBePositive() {
    assertThrows(
        IllegalArgumentException.class, () -> new BuyXGetY(new Sku("X"), 0, new Sku("Y"), 1));
    assertThrows(
        IllegalArgumentException.class, () -> new BuyXGetY(new Sku("X"), 1, new Sku("Y"), 0));
  }
}
