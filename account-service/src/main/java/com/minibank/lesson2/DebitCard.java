package com.minibank.lesson2;

public class DebitCard {
  private final String cardNumber;
  private long dailyLimit;
  private long spentToday;
  private boolean locked;

  public DebitCard(String cardNumber, long dailyLimit) {
    requireValidCardNumber(cardNumber);
    requirePositiveDailyLimit(dailyLimit);

    this.cardNumber = cardNumber;
    this.dailyLimit = dailyLimit;
  }

  private static void requireValidCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.isBlank()) {
      throw new IllegalArgumentException("cardNumber cannot be null");
    }
  }

  private static void requirePositiveDailyLimit(long dailyLimit) {
    if (dailyLimit <= 0) {
      throw new IllegalArgumentException("daily limit must be positive");
    }
  }

  private static void requirePositiveAmount(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setDailyLimit(long dailyLimit) {
    requirePositiveDailyLimit(dailyLimit);
    requireNotBelowSpentToday(dailyLimit);

    this.dailyLimit = dailyLimit;
  }

  public void lock() {
    this.locked = true;
  }

  public void unlock() {
    this.locked = false;
  }

  public void spend(long amount) {
    requireNotLocked();
    requirePositiveAmount(amount);
    requireWithinDailyLimit(amount);

    spentToday = spentToday + amount;
  }

  public void resetDailySpending() {
    spentToday = 0;
  }

  private void requireNotBelowSpentToday(long dailyLimit) {
    if (dailyLimit < spentToday) {
      throw new IllegalArgumentException("You cannot set daily limit less than spent today");
    }
  }

  private void requireWithinDailyLimit(long amount) {
    if (spentToday + amount > dailyLimit) {
      throw new IllegalStateException("Cannot spend more than daily limit");
    }
  }

  private void requireNotLocked() {
    if (locked) {
      throw new IllegalStateException("Your card has been locked");
    }
  }

}
