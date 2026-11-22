package com.minibank.lesson4;

public class BusinessAccount extends Account {

  private final long monthlyFee;

  public BusinessAccount(String id, Customer customer, long monthlyFee) {
    super(id, customer);
    if (monthlyFee < 0) {
      throw new IllegalArgumentException("Monthly fee must not be negative");
    }
    this.monthlyFee = monthlyFee;
  }

  public void deductMonthlyFee() {
    if (monthlyFee > 0) {
      withdraw(monthlyFee);
    }
  }

  @Override
  public void endOfMonth() {
    deductMonthlyFee();
  }
}
