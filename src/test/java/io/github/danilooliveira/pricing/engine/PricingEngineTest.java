package io.github.danilooliveira.pricing.engine;

import static org.junit.jupiter.api.Assertions.*;

import io.github.danilooliveira.pricing.model.Money;
import io.github.danilooliveira.pricing.model.Order;
import io.github.danilooliveira.pricing.promo.BuyXGetY;
import io.github.danilooliveira.pricing.promo.FixedAmountOff;
import io.github.danilooliveira.pricing.promo.PercentageOff;
import io.github.danilooliveira.pricing.promo.Promotion;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingEngineTest {

  private final PricingEngine engine = new PricingEngine();

  @Test
  void percentageAndFixedAmount() {
    Order o = PricingEngine.orderOf("O1", "BRL",
        "A", 2, 10.00,  // 20.00
        "B", 1, 5.00);  //  5.00  => gross 25.00

    List<Promotion> promos = List.of(
        new PercentageOff(new BigDecimal("0.10")),           // 10% -> 2.50
        new FixedAmountOff(Money.of(3.00, "BRL")));          // 3.00

    var priced = engine.price(o, promos);
    assertEquals(0, priced.gross().amount().compareTo(Money.of(25.00, "BRL").amount()));
    assertEquals(0, priced.discount().amount().compareTo(Money.of(5.50, "BRL").amount()));
    assertEquals(0, priced.net().amount().compareTo(Money.of(19.50, "BRL").amount()));
  }

  @Test
  void buyXGetYDifferentSkus() {
    Order o = PricingEngine.orderOf("O2", "BRL",
        "X", 4, 10.00,  // 40.00
        "Y", 3, 6.00);  // 18.00  => gross 58.00

    // A cada 2 de X, 1 de Y grátis. Com 4 X → 2 Y grátis. Y comprado = 3 → desconto 2*6=12
    List<Promotion> promos = List.of(new BuyXGetY(
        new io.github.danilooliveira.pricing.model.Sku("X"), 2,
        new io.github.danilooliveira.pricing.model.Sku("Y"), 1));

    var priced = engine.price(o, promos);
    assertEquals(0, priced.discount().amount().compareTo(Money.of(12.00, "BRL").amount()));
    assertEquals(0, priced.net().amount().compareTo(Money.of(46.00, "BRL").amount()));
  }

  @Test
  void buyOneGetOneSameSku() {
    Order o = PricingEngine.orderOf("O3", "BRL",
        "A", 3, 10.00); // gross 30.00

    // BOGO: a cada 1 A, 1 A gratis -> com 3 A, ganha 3/1=3 grupos → até 3 grátis,
    // mas só 3 comprados => desconto 3 unidades? Não. Política comum é
    // "cada par 2 pelo preço de 1": use qtyY=1 com groups=floor(qty/2).
    // Para BOGO clássico: qtyX=2, qtyY=1.
    var promo = new BuyXGetY(
        new io.github.danilooliveira.pricing.model.Sku("A"), 2,
        new io.github.danilooliveira.pricing.model.Sku("A"), 1);

    var priced = engine.price(o, List.of(promo));
    // qtyX=3 -> groups=floor(3/2)=1 → 1 unidade grátis -> 10.00 de desconto.
    assertEquals(0, priced.discount().amount().compareTo(Money.of(10.00, "BRL").amount()));
    assertEquals(0, priced.net().amount().compareTo(Money.of(20.00, "BRL").amount()));
  }
}
