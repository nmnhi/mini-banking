package com.minibank.account;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Transaction(String id, TransactionType type, Money amount, Instant createdAt) {

  public Transaction {
    Objects.requireNonNull(id, "id cannot be null");
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(amount, "amount cannot be null");
    Objects.requireNonNull(createdAt, "createdAt cannot be null");
  }

  public static Transaction of(TransactionType type, Money amount) {
    return new Transaction(UUID.randomUUID().toString(), type, amount, Instant.now());
  }
}
