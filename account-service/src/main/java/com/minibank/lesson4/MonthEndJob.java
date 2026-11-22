package com.minibank.lesson4;

public class MonthEndJob {

  public void run(Account[] accounts) {
    for (Account account : accounts) {
      account.endOfMonth();
    }
  }
}
