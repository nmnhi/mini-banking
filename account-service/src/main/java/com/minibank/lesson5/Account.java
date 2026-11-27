package com.minibank.lesson5;

public abstract class Account {

  private static final long MAX_WITHDRAWAL = 50_000_000;
  private final String id;
  private final Customer customer;
  private long balance;

  public Account(String id, Customer customer) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Id cannot be null or blank");
    }
    if (customer == null) {
      throw new IllegalArgumentException("Customer cannot be null");
    }
    this.id = id;
    this.customer = customer;
  }

  public String getId() {
    return id;
  }

  public long getBalance() {
    return balance;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void deposit(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    balance = balance + amount;
  }

  public void withdraw(long amount) {
    withdrawWithFee(amount, 0);
  }

  public void withdrawWithFee(long amount, long fee) {
    checkValidAmountAndFee(amount, fee);
    if (balance - amount < fee) {
      throw new IllegalStateException("Insufficient balance");
    }

    balance = balance - (amount + fee);
  }

  private void checkValidAmountAndFee(long amount, long fee) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (amount > MAX_WITHDRAWAL) {
      throw new IllegalArgumentException("Amount exceeds limit of " + MAX_WITHDRAWAL);
    }
    if (fee < 0) {
      throw new IllegalArgumentException("Fee cannot be negative");
    }
  }

  public abstract void endOfMonth();
}
