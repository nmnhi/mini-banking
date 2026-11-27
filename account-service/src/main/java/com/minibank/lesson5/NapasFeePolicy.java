package com.minibank.lesson5;

public class NapasFeePolicy implements TransferFeePolicy {
  @Override
  public long calculate(long amount) {
    return 3_300;
  }

  @Override
  public String type() {
    return "NAPAS";
  }
}
