package com.minibank.lesson5;

import java.time.Instant;

public class AuditLogger {

  public void logSuccess(String txId, Instant timestamp, String type, String fromId, String toId,
      long amount, long fee) {
    System.out.println(baseLine(txId, timestamp, type, fromId, toId, amount, String.valueOf(fee))
        + " status=SUCCESS");
  }

  public void logFailed(String txId, Instant timestamp, String type, String fromId, String toId,
      long amount, Long fee, String reason) {
    String feeText = fee == null ? "-" : String.valueOf(fee);
    System.out.println(baseLine(txId, timestamp, type, fromId, toId, amount, feeText)
        + " status=FAILED reason=\"" + reason + "\"");
  }

  private String baseLine(String txId, Instant timestamp, String type, String fromId, String toId,
      long amount, String feeText) {
    return String.format("[AUDIT] txId=%s time=%s type=%s from=%s to=%s amount=%d fee=%s",
        txId, timestamp, type, fromId, toId, amount, feeText);
  }
}
