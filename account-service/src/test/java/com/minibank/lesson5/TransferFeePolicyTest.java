package com.minibank.lesson5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class TransferFeePolicyTest {

  @Test
  void internal_isFree() {
    InternalFeePolicy policy = new InternalFeePolicy();

    assertThat(policy.calculate(1_000_000)).isZero();
    assertThat(policy.type()).isEqualTo("INTERNAL");
  }

  @Test
  void napas_chargesFlatFee() {
    NapasFeePolicy policy = new NapasFeePolicy();

    assertThat(policy.calculate(10_000)).isEqualTo(3_300);
    assertThat(policy.calculate(50_000_000)).isEqualTo(3_300);
    assertThat(policy.type()).isEqualTo("NAPAS");
  }

  @Test
  void swift_belowThreshold_chargesMinimumFee() {
    SwiftFeePolicy policy = new SwiftFeePolicy();

    assertThat(policy.calculate(30_000_000)).isEqualTo(200_000);
    assertThat(policy.type()).isEqualTo("SWIFT");
  }

  @Test
  void swift_aboveThreshold_chargesPointOnePercent() {
    SwiftFeePolicy policy = new SwiftFeePolicy();

    assertThat(policy.calculate(200_000_000)).isEqualTo(200_000);
    assertThat(policy.calculate(500_000_000)).isEqualTo(500_000);
  }
}
