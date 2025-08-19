package io.github.danilooliveira.pricing.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Order(String id, List<LineItem> items, String coupon) {
    public Order {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        items = List.copyOf(Objects.requireNonNull(items, "items"));
        if (items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
    }

    public String currency() {
        return items.get(0).unitPrice().currency();
    }

    public Money gross() {
        String ccy = currency();
        Money total = Money.zero(ccy);
        for (LineItem it : items) {
            if (!ccy.equals(it.unitPrice().currency())) {
                throw new IllegalArgumentException("mixed currencies in order");
            }
            total = total.add(it.gross());
        }
        return total;
    }

    public int qtyOf(Sku sku) {
        return items.stream().filter(i -> i.sku().equals(sku)).mapToInt(LineItem::quantity).sum();
    }

    public Optional<Money> unitPriceOf(Sku sku) {
        return items.stream().filter(i -> i.sku().equals(sku)).findFirst().map(LineItem::unitPrice);
    }

}
