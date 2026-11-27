package com.minibank.lesson5;

public interface TransferFeePolicy {
  long calculate(long amount);

  String type();
}

