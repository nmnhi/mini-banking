package com.minibank.account;

import java.util.Objects;

public final class Money {

  private final long amount;
  private final String currency;

  public Money(long amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount must not be negative, but was " + amount);
    }

    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("Currency must not be blank");
    }

    this.amount = amount;
    this.currency = currency;
  }

  public long getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }


  private void requireSameCurrency(Money other) {
    if (!other.getCurrency().equals(currency)) {
      throw new IllegalArgumentException("Currency must match");
    }
  }

  public Money add(Money other) {
    requireSameCurrency(other);

    return new Money(other.getAmount() + amount, other.getCurrency());
  }

  public Money subtract(Money other) {
    requireSameCurrency(other);

    long newAmount = amount - other.getAmount();

    if (newAmount < 0) {
      throw new IllegalArgumentException("Cannot subtract money with amount " + other.getAmount());
    }

    return new Money(newAmount, other.getCurrency());
  }

  @Override
  public String toString() {
    return amount + " " + currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;                              // (a) cùng 1 object, chắc chắn bằng
    }
    if (o == null || getClass() != o.getClass()) {
      return false; // (b) null hoặc khác loại thì không bằng
    }
    Money other = (Money) o;                                 // (c) giờ mới ép kiểu an toàn
    return amount == other.amount                            // (d) long là primitive nên dùng ==
        && currency.equals(other.currency);                  //     String là object nên dùng equals
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);  // import java.util.Object
  }

}
