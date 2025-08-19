package io.github.danilooliveira.pricing.model;

import java.util.Objects;

public record OrderPriced(String orderId, Money gross, Money discount, Money net, String currency) {

  public OrderPriced {
    Objects.requireNonNull(orderId, "orderId");
    Objects.requireNonNull(gross, "gross");
    Objects.requireNonNull(discount, "discount");
    Objects.requireNonNull(net, "net");
    Objects.requireNonNull(currency, "currency");
    if (!gross.currency().equals(currency)
        || !discount.currency().equals(currency)
        || !net.currency().equals(currency)) {
      throw new IllegalArgumentException("currency mismatch");
    }
  }
}
