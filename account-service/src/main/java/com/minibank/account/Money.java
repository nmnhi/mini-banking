package com.minibank.account;

public record Money(long amount, String currency) {

  public Money {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount must not be negative, but was " + amount);
    }

    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("Currency must not be blank");
    }
  }

  private void requireSameCurrency(Money other) {
    if (!other.currency().equals(currency)) {
      throw new IllegalArgumentException("Currency must match");
    }
  }

  // So sánh tiền phải cùng loại tiền; logic so sánh nằm trong Money (Tell, don't ask)
  public boolean isLessThan(Money other) {
    requireSameCurrency(other);

    return amount < other.amount();
  }

  public Money add(Money other) {
    requireSameCurrency(other);

    return new Money(other.amount() + amount, other.currency());
  }

  public Money subtract(Money other) {
    requireSameCurrency(other);

    long newAmount = amount - other.amount();

    if (newAmount < 0) {
      throw new IllegalArgumentException("Cannot subtract money with amount " + other.amount());
    }

    return new Money(newAmount, other.currency());
  }

  @Override
  public String toString() {
    return amount + " " + currency;
  }
}
