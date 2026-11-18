package com.minibank.lesson1;

public class Wallet {
  private String ownerName;
  private long balance;

  public Wallet(String ownerName, long balance) {
    if (balance < 0) {
      throw new IllegalArgumentException("balance cannot be negative");
    }

    requireValidOwnerName(ownerName);

    this.ownerName = ownerName;
    this.balance = balance;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    requireValidOwnerName(ownerName);
    this.ownerName = ownerName;
  }

  public long getBalance() {
    return balance;
  }

  public void deposit(long amount) {
    requirePositiveAmount(amount);
    balance = balance + amount;
  }

  public void pay(long amount) {
    requirePositiveAmount(amount);

    if (balance < amount) {
      throw new InsufficientBalanceException(
              "Insufficient balance: balance=" + balance + ", amount=" + amount);
    }

    balance = balance - amount;
  }

  private static void requirePositiveAmount(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
  }

  private static void requireValidOwnerName(String ownerName) {
    if (ownerName == null || ownerName.isBlank()) {
      throw new IllegalArgumentException("ownerName cannot be null or blank");
    }
  }
}
