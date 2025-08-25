# java-core-advanced — Pricing & Promotions (Java 17+)

A small, production-style showcase for **immutability**, **sealed hierarchies**, **custom collectors**, and a **type-safe rule pipeline** in modern Java.
It models a pricing engine for retail scenarios (SKUs, promotions, totals) and ships with tests, lint/format, and CI-friendly config.

## ✨ Highlights

* **Records** for value objects (`Money`, `Sku`, `LineItem`, `Order`, `OrderPriced`)
* **Sealed promotions**: `PercentageOff`, `FixedAmountOff`, `BuyXGetY`
* **PricingEngine** with currency checks & discount capping
* **Type-safe RuleRegistry** using `Function<? super T, ? extends T>` (no casts)
* **Custom Collector** for stats (totals & average ticket)
* Tooling: **JUnit 5**, **Spotless** (google-java-format), **Checkstyle** (Google-like, 2 spaces / 100 cols)

---

## 🧱 Project layout

```
src/main/java/io/github/danilooliveira/pricing/
├─ model/  (records & invariants)
├─ promo/  (sealed hierarchy)
├─ engine/ (rules & pricing)
└─ stats/  (collector & result)
src/test/java/io/github/danilooliveira/pricing/
```

---

## 🚀 Quickstart

### Requirements

* JDK **17+**
* Maven **3.9+**

### Build & test

```bash
mvn -q verify
```

### Format & lint

```bash
# auto-format all sources
mvn -q spotless:apply

# check formatting and style (CI-friendly)
mvn -q -DskipTests spotless:check checkstyle:check
```

### Optional: Git pre-commit hook

```bash
mkdir -p .githooks
git config core.hooksPath .githooks
cat > .githooks/pre-commit <<'SH'
#!/bin/sh
set -e
echo "▶ Spotless: formatting..."
mvn -q -DskipTests spotless:apply
git add -A
echo "▶ Lint: spotless:check + checkstyle:check..."
mvn -q -DskipTests spotless:check checkstyle:check
echo "✔ pre-commit ok"
SH
chmod +x .githooks/pre-commit
```

---

## 💡 Usage examples

### 1) Pricing an order with promotions

```java
var engine = new PricingEngine();

var order = PricingEngine.orderOf("O1", "BRL",
    "A", 2, 10.00,   // 20.00
    "B", 1,  5.00);  //  5.00    => gross 25.00

List<Promotion> promos = List.of(
    new PercentageOff(new BigDecimal("0.10")),   // -2.50
    new FixedAmountOff(Money.of(3.00, "BRL")));  // -3.00

OrderPriced priced = engine.price(order, promos);
// priced.gross() = 25.00 BRL
// priced.discount() = 5.50 BRL
// priced.net() = 19.50 BRL
```

### 2) Buy X Get Y (same or different SKUs)

```java
var order = PricingEngine.orderOf("O2", "BRL",
    "X", 4, 10.00,
    "Y", 3,  6.00);

var bogo = new BuyXGetY(new Sku("X"), 2, new Sku("Y"), 1); // each 2 of X grants 1 Y
var priced = engine.price(order, List.of(bogo));
// discount = min(groups*qtyY, qtyYBought) * unitPrice(Y) = min(2*1,3)*6 = 12
```

### 3) Aggregating stats with a custom Collector

```java
var promo10 = new PercentageOff(new BigDecimal("0.10"));
var pricedOrders = Stream.of(
    PricingEngine.orderOf("O1", "BRL", "A", 2, 10.00),
    PricingEngine.orderOf("O2", "BRL", "B", 1, 15.00),
    PricingEngine.orderOf("O3", "BRL", "C", 3,  5.00))
  .map(o -> engine.price(o, List.of(promo10)))
  .toList();

OrderStatistics stats = pricedOrders.stream().collect(new OrderStatisticsCollector());
// stats.orders()        == 3
// stats.grossTotal()    == 50.00 BRL
// stats.discountTotal() ==  5.00 BRL
// stats.netTotal()      == 45.00 BRL
// stats.averageTicket() == 15.00
```

### 4) Composing rules in the type-safe registry

```java
Rule<OrderPriced> capMinNetZero = op -> {
  var net = op.net();
  if (net.amount().signum() < 0) {
    return new OrderPriced(op.orderId(), op.gross(), op.gross(), Money.zero(op.currency()), op.currency());
  }
  return op;
};

var reg = new RuleRegistry<OrderPriced>()
    .registerRule(capMinNetZero)
    .register(op -> op); // add more transforms

OrderPriced finalState = reg.applyAll(priced);
```

---

## 🧪 Testing

* Unit tests live under `src/test/java`. Run:

  ```bash
  mvn -q test
  ```
* Focus on invariants (records), promotion validation, pricing math, and collector aggregation.

---

## 🔧 Tooling

* **Spotless**: Google Java Format `1.28.0`
* **Checkstyle**: Google-like rules (`config/checkstyle/checkstyle.xml`)
* **JUnit 5**: Jupiter BOM with modern engine

---

## 📌 Roadmap (ideas)

* Item-level promotions and stacking policies
* Taxes/VAT examples with jurisdictions
* More collectors (top-N SKUs, revenue per family)
* Property-based tests
