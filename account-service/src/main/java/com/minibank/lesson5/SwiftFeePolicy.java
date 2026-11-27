package com.minibank.lesson5;

public class SwiftFeePolicy implements TransferFeePolicy {
  @Override
  public long calculate(long amount) {
    return Math.max(200_000, amount / 1000);
  }

  @Override
  public String type() {
    return "SWIFT";
  }
}
