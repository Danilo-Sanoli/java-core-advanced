package io.github.danilooliveira.pricing.engine;

public interface Rule<T> { // T can be Order, LineItem, etc.
  T apply(T input);
}
