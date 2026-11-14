package com.minibank.account;

public class Account {

  private final String id;
  private long balance;

  public Account(String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id must not be blank");
    }
    this.id = id;
    this.balance = 0;
  }

  public String getId() {
    return id;
  }

  public long getBalance() {
    return balance;
  }

  public void deposit(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be greater than 0, but was " + amount);
    }

    balance = balance + amount;
  }

  public void withdraw(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be greater than 0, but was " + amount);
    }

    if (balance < amount) {
      throw new InsufficientBalanceException("Insufficient balance: balance= " + balance +
          ", " + "requested= " + amount);
    }

    balance = balance - amount;
  }

}
