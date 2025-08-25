package io.github.danilooliveira.pricing.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ModelValidationTest {

  @Test
  void skuMustNotBeBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Sku(" "));
  }

  @Test
  void lineItemQtyMustBePositive() {
    var sku = new Sku("A");
    var price = Money.of(10.0, "BRL");
    assertThrows(IllegalArgumentException.class, () -> new LineItem(sku, 0, price));
  }

  @Test
  void orderMustBeImmutableAndNonEmpty() {
    var a = new LineItem(new Sku("A"), 1, Money.of(10.0, "BRL"));
    var list = new java.util.ArrayList<>(List.of(a));
    var order = new Order("O1", list, null);
    list.clear(); // não deve afetar o pedido
    assertEquals(1, order.items().size());
    assertThrows(UnsupportedOperationException.class, () -> order.items().add(a));
  }

  @Test
  void moneyOperations() {
    Money x = Money.of(10.0, "BRL");
    Money y = Money.of(2.5, "BRL");
    assertEquals(0, x.add(y).amount().compareTo(Money.of(12.5, "BRL").amount()));
    assertEquals(0, x.subtract(y).amount().compareTo(Money.of(7.5, "BRL").amount()));
  }
}
