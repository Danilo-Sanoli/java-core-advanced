package io.github.danilooliveira.pricing.stats;

import io.github.danilooliveira.pricing.model.Money;
import io.github.danilooliveira.pricing.model.OrderPriced;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class OrderStatisticsCollector
    implements Collector<OrderPriced, OrderStatisticsCollector.Acc, OrderStatistics> {

  static final class Acc {
    int orders = 0;
    String currency = null;
    BigDecimal gross = BigDecimal.ZERO;
    BigDecimal discount = BigDecimal.ZERO;
  }

  @Override
  public Supplier<Acc> supplier() {
    return Acc::new;
  }

  @Override
  public BiConsumer<Acc, OrderPriced> accumulator() {
    return (a, p) -> {
      if (a.currency == null) {
        a.currency = p.currency();
      } else if (!a.currency.equals(p.currency())) {
        throw new IllegalArgumentException("mixed currencies in stats");
      }
      a.orders++;
      a.gross = a.gross.add(p.gross().amount());
      a.discount = a.discount.add(p.discount().amount());
    };
  }

  @Override
  public BinaryOperator<Acc> combiner() {
    return (x, y) -> {
      if (x.currency == null) {
        x.currency = y.currency;
      } else if (y.currency != null && !x.currency.equals(y.currency)) {
        throw new IllegalArgumentException("mixed currencies in stats");
      }
      x.orders += y.orders;
      x.gross = x.gross.add(y.gross);
      x.discount = x.discount.add(y.discount);
      return x;
    };
  }

  @Override
  public Function<Acc, OrderStatistics> finisher() {
    return a -> {
      if (a.currency == null) {
        a.currency = "BRL";
      }
      Money gross = new Money(a.gross, a.currency);
      Money discount = new Money(a.discount, a.currency);
      Money net = gross.subtract(discount);
      BigDecimal avg =
          a.orders == 0
              ? BigDecimal.ZERO
              : net.amount().divide(BigDecimal.valueOf(a.orders), 2, RoundingMode.HALF_UP);
      return new OrderStatistics(a.orders, gross, discount, net, avg);
    };
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Set.of();
  }
}
