package com.minibank.lesson5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class TransferMessageFormatterTest {

  private final TransferMessageFormatter formatter = new TransferMessageFormatter();

  @Test
  void forSender_includesAmountReceiverFeeAndBalance() {
    String message = formatter.forSender(500_000, 3_300, "CHK-1", 8_496_700);

    assertThat(message)
        .isEqualTo("You transferred 500,000 VND to CHK-1. Fee 3,300 VND. Balance: 8,496,700 VND");
  }

  @Test
  void forReceiver_includesAmountAndSender() {
    String message = formatter.forReceiver(1_000_000, "SAV-1");

    assertThat(message).isEqualTo("You received 1,000,000 VND from SAV-1");
  }
}
