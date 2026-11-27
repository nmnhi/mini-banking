package com.minibank.lesson5;

public class QrFeePolicy implements TransferFeePolicy {
  @Override
  public long calculate(long amount) {
    return 1_000;
  }

  @Override
  public String type() {
    return "QR";
  }
}
