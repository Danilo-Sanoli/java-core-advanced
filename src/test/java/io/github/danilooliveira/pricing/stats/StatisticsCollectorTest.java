package io.github.danilooliveira.pricing.stats;

import static org.junit.jupiter.api.Assertions.*;

import io.github.danilooliveira.pricing.engine.PricingEngine;
import io.github.danilooliveira.pricing.model.OrderPriced;
import io.github.danilooliveira.pricing.promo.PercentageOff;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StatisticsCollectorTest {

  @Test
  void aggregateThreeOrders() {
    var engine = new PricingEngine();

    var o1 = PricingEngine.orderOf("O1", "BRL", "A", 2, 10.00); // 20.00
    var o2 = PricingEngine.orderOf("O2", "BRL", "B", 1, 15.00); // 15.00
    var o3 = PricingEngine.orderOf("O3", "BRL", "C", 3, 5.00);  // 15.00
    var promo = new PercentageOff(new BigDecimal("0.10"));      // 10% em todos

    List<OrderPriced> priced =
        Stream.of(o1, o2, o3).map(o -> engine.price(o, List.of(promo))).toList();

    var stats = priced.stream().collect(new OrderStatisticsCollector());
    assertEquals(3, stats.orders());
    assertEquals(0, stats.grossTotal().amount().compareTo(
        io.github.danilooliveira.pricing.model.Money.of(50.00, "BRL").amount()));
    // desconto 10% de cada -> 5.00 total
    assertEquals(0, stats.discountTotal().amount().compareTo(
        io.github.danilooliveira.pricing.model.Money.of(5.00, "BRL").amount()));
    assertEquals(0, stats.netTotal().amount().compareTo(
        io.github.danilooliveira.pricing.model.Money.of(45.00, "BRL").amount()));
    assertEquals(0, stats.averageTicket().compareTo(new BigDecimal("15.00")));
  }
}
