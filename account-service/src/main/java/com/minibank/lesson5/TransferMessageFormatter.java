package com.minibank.lesson5;

public class TransferMessageFormatter {
  public String forSender(long amount, long fee, String toAccountId, long balanceAfter) {
    return String.format("You transferred %,d VND to %s. Fee %,d VND. Balance: %,d VND",
            amount,
            toAccountId,
            fee,
            balanceAfter
    );
  }

  public String forReceiver(long amount, String fromAccountId) {
    return String.format("You received %,d VND from %s", amount, fromAccountId);
  }


}
