package com.minibank.lesson4;

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
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (amount > MAX_WITHDRAWAL) {
      throw new IllegalArgumentException("Cannot withdraw more than " + MAX_WITHDRAWAL);
    }
    if (balance < amount) {
      throw new IllegalStateException("Insufficient balance");
    }
    balance = balance - amount;
  }

  public abstract void endOfMonth();
}
