package com.minibank.lesson3;

public class WithdrawalService {
  private final Notifier notifier;

  public WithdrawalService(Notifier notifier) {
    if (notifier == null) {
      throw new IllegalArgumentException("Notifier cannot be null");
    }
    this.notifier = notifier;
  }

  public void withdraw(Account account, long amount) {
    account.withdraw(amount);
    notifier.send(
            account.getCustomer().getEmail(),
            "You have withdrawn " + amount + " from account " + account.getId()
    );
  }
}
