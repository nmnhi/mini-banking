package com.minibank.lesson4;

public class StudentAccount extends Account {

  public StudentAccount(String id, Customer customer) {
    super(id, customer);
  }

  @Override
  public void endOfMonth() {
    // Student accounts: no interest, no fee
  }
}
