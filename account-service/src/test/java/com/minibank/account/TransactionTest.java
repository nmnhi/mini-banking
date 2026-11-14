package com.minibank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class TransactionTest {

  private static final Money AMOUNT = new Money(100, "VND");

  @Test
  void of_generatesIdAndTimestamp() {
    Transaction tx = Transaction.of(TransactionType.DEPOSIT, AMOUNT);

    assertThat(tx.id()).isNotBlank();
    assertThat(tx.createdAt()).isNotNull();
    assertThat(tx.type()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(tx.amount()).isEqualTo(AMOUNT);
  }

  @Test
  void of_generatesUniqueIds() {
    Transaction a = Transaction.of(TransactionType.DEPOSIT, AMOUNT);
    Transaction b = Transaction.of(TransactionType.DEPOSIT, AMOUNT);

    assertThat(a.id()).isNotEqualTo(b.id());
  }

  @Test
  void constructor_nullFields_throwNullPointerException() {
    Instant now = Instant.now();

    assertThatThrownBy(() -> new Transaction(null, TransactionType.DEPOSIT, AMOUNT, now))
        .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
    assertThatThrownBy(() -> new Transaction("T1", null, AMOUNT, now))
        .isInstanceOf(NullPointerException.class).hasMessageContaining("type");
    assertThatThrownBy(() -> new Transaction("T1", TransactionType.DEPOSIT, null, now))
        .isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    assertThatThrownBy(() -> new Transaction("T1", TransactionType.DEPOSIT, AMOUNT, null))
        .isInstanceOf(NullPointerException.class).hasMessageContaining("createdAt");
  }
}
