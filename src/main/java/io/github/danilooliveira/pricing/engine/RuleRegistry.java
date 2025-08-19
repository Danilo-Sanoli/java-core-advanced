package io.github.danilooliveira.pricing.engine;

import java.util.ArrayList;
import java.util.List;

public class RuleRegistry<T> {

  private final List<Rule<? super T>> rules = new ArrayList<>();

  public RuleRegistry<T> register(Rule<? super T> rule) {
    rules.add(rule);
    return this;
  }

  public T applyAll(T input) {
    T current = input;
    for (Rule<? super T> r : rules) {
      @SuppressWarnings("unchecked")
      Rule<T> cast = (Rule<T>) r;
      current = cast.apply(current);
    }
    return current;
  }
}
