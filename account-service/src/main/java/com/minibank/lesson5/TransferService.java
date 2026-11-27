package com.minibank.lesson5;

import java.time.Instant;
import java.util.UUID;

public class TransferService {
  private final Notifier notifier;
  private final TransferMessageFormatter transferMessageFormatter;
  private final AuditLogger auditLogger;

  public TransferService(Notifier notifier, TransferMessageFormatter transferMessageFormatter, AuditLogger auditLogger) {
    if (notifier == null) {
      throw new IllegalArgumentException("Notifier cannot be null");
    }

    if (transferMessageFormatter == null) {
      throw new IllegalArgumentException("TransferMessageFormatter cannot be null");
    }

    if (auditLogger == null) {
      throw new IllegalArgumentException("AuditLogger cannot be null");
    }
    this.notifier = notifier;
    this.transferMessageFormatter = transferMessageFormatter;
    this.auditLogger = auditLogger;
  }

  public void transfer(Account from, Account to, long amount, TransferFeePolicy feePolicy) {
    String txId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    Long fee = null; // null = chưa tính được phí, log sẽ ghi "-"

    try {
      validate(from, to, amount, feePolicy);
      fee = feePolicy.calculate(amount);
      from.withdrawWithFee(amount, fee);
      to.deposit(amount);
      auditLogger.logSuccess(txId, now, feePolicy.type(), from.getId(), to.getId(), amount, fee);
    } catch (RuntimeException e) {
      auditLogger.logFailed(txId, now,
          feePolicy == null ? "-" : feePolicy.type(),
          from == null ? "-" : from.getId(),
          to == null ? "-" : to.getId(),
          amount, fee, e.getMessage());
      throw e;
    }

    notifier.send(from.getCustomer(),
        transferMessageFormatter.forSender(amount, fee, to.getId(), from.getBalance()));
    notifier.send(to.getCustomer(),
        transferMessageFormatter.forReceiver(amount, from.getId()));
  }

  private void validate(Account from, Account to, long amount, TransferFeePolicy feePolicy) {
    if (from == null) {
      throw new IllegalArgumentException("Sender cannot be null");
    }

    if (to == null) {
      throw new IllegalArgumentException("Receiver cannot be null");
    }

    if (feePolicy == null) {
      throw new IllegalArgumentException("FeePolicy cannot be null");
    }

    if (from.getId().equals(to.getId())) {
      throw new IllegalArgumentException("Sender and Receiver cannot be same");
    }

    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
  }
}
