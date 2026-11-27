package com.minibank.lesson5;

public class SavingsAccount extends Account {

  private final long interestRateBps;   // interest rate in basis points, 50 = 0.5% per month

  public SavingsAccount(String id, Customer customer, long interestRateBps) {
    super(id, customer);
    if (interestRateBps < 0) {
      throw new IllegalArgumentException("Interest rate must not be negative");
    }
    this.interestRateBps = interestRateBps;
  }

  public void addMonthlyInterest() {
    long interest = getBalance() * interestRateBps / 10_000;
    if (interest > 0) {
      deposit(interest);
    }
  }

  @Override
  public void endOfMonth() {
    addMonthlyInterest();
  }
}
