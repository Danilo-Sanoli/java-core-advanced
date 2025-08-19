package io.github.danilooliveira.pricing.engine;

import io.github.danilooliveira.pricing.model.LineItem;
import io.github.danilooliveira.pricing.model.Money;
import io.github.danilooliveira.pricing.model.Order;
import io.github.danilooliveira.pricing.model.OrderPriced;
import io.github.danilooliveira.pricing.promo.BuyXGetY;
import io.github.danilooliveira.pricing.promo.FixedAmountOff;
import io.github.danilooliveira.pricing.promo.PercentageOff;
import io.github.danilooliveira.pricing.promo.Promotion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class PricingEngine {

  public OrderPriced price(Order order, List<Promotion> promotions) {
    Objects.requireNonNull(order, "order");
    Objects.requireNonNull(promotions, "promotions");
    String ccy = order.currency();
    Money gross = order.gross();
    Money discount = Money.zero(ccy);

    for (Promotion p : promotions) {
      if (p instanceof PercentageOff perc) {
        Money d = gross.multiply(perc.percent());
        discount = discount.add(d);
      } else if (p instanceof FixedAmountOff fixed) {
        Money remaining = gross.subtract(discount);
        Money d = fixed.amount().min(remaining);
        if (!d.currency().equals(ccy)) {
          throw new IllegalArgumentException("promotion currency mismatch");
        }
        discount = discount.add(d);
      } else if (p instanceof BuyXGetY bxgy) {
        int qtyX = order.qtyOf(bxgy.skuX());
        int qtyYBought = order.qtyOf(bxgy.skuY());
        if (qtyX <= 0 || qtyYBought <= 0) {
          continue;
        }
        int groups = qtyX / bxgy.qtyX();
        if (groups <= 0) {
          continue;
        }
        int freeUnits = Math.min(groups * bxgy.qtyY(), qtyYBought);
        var unitPriceOpt = order.unitPriceOf(bxgy.skuY());
        if (unitPriceOpt.isEmpty()) {
          continue;
        }
        Money unit = unitPriceOpt.get();
        Money d = unit.multiply(freeUnits);
        discount = discount.add(d);
      }
    }

    if (discount.amount().compareTo(gross.amount()) > 0) {
      discount = gross;
    }
    Money net = gross.subtract(discount);

    return new OrderPriced(order.id(), gross, discount, net, ccy);
  }

  public static Order orderOf(String id, String ccy, Object... triplets) {
    java.util.List<LineItem> items = new java.util.ArrayList<>();
    for (int i = 0; i < triplets.length; i += 3) {
      String sku = (String) triplets[i];
      int qty = (Integer) triplets[i + 1];
      double unit = (Double) triplets[i + 2];
      items.add(
          new LineItem(
              new io.github.danilooliveira.pricing.model.Sku(sku), qty, Money.of(unit, ccy)));
    }
    return new Order(id, items, null);
  }

  private static Money multiply(Money money, BigDecimal factor) {
    return money.multiply(factor);
  }
}
