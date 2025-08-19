package io.github.danilooliveira.pricing.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) implements Comparable<Money> {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(double value, String currency) {
        return new Money(BigDecimal.valueOf(value), currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(int factor) {
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    public Money min(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) <= 0 ? this : other;
    }

    public Money max(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) >= 0 ? this : other;
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("different currencies");
        }
    }

    @Override
    public int compareTo(Money o) {
        if (!currency.equals(o.currency)) {
            throw new IllegalArgumentException("different currencies");
        }
        return amount.compareTo(o.amount);
    }
}
