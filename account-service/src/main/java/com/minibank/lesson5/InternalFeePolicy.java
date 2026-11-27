package com.minibank.lesson5;

public class InternalFeePolicy implements TransferFeePolicy {
  @Override
  public long calculate(long amount) {
    return 0;
  }

  @Override
  public String type() {
    return "INTERNAL";
  }
}
