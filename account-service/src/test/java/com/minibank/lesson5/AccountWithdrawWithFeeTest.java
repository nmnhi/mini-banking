package com.minibank.lesson5;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class AccountWithdrawWithFeeTest {

  private final Customer customer = new Customer("a@minibank.com", "A", "0901000001");

  private CheckingAccount accountWith(long balance) {
    CheckingAccount account = new CheckingAccount("CHK-1", customer, 0);
    account.deposit(balance);
    return account;
  }

  @Test
  void withdrawWithFee_deductsAmountPlusFee() {
    CheckingAccount account = accountWith(1_000_000);

    account.withdrawWithFee(500_000, 3_300);

    assertThat(account.getBalance()).isEqualTo(496_700);
  }

  @Test
  void withdrawWithFee_exactBalance_leavesZero() {
    CheckingAccount account = accountWith(503_300);

    account.withdrawWithFee(500_000, 3_300);

    assertThat(account.getBalance()).isZero();
  }

  @Test
  void withdrawWithFee_enoughForAmountButNotFee_throwsAndKeepsBalance() {
    CheckingAccount account = accountWith(500_000);

    assertThatThrownBy(() -> account.withdrawWithFee(499_000, 3_300))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Insufficient balance");
    assertThat(account.getBalance()).isEqualTo(500_000);
  }

  @Test
  void withdrawWithFee_amountGreaterThanBalance_throws() {
    CheckingAccount account = accountWith(100_000);

    assertThatThrownBy(() -> account.withdrawWithFee(200_000, 0))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withdrawWithFee_negativeFee_throws() {
    CheckingAccount account = accountWith(1_000_000);

    assertThatThrownBy(() -> account.withdrawWithFee(100_000, -1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fee cannot be negative");
    assertThat(account.getBalance()).isEqualTo(1_000_000);
  }

  @Test
  void withdrawWithFee_nonPositiveAmount_throws() {
    CheckingAccount account = accountWith(1_000_000);

    assertThatThrownBy(() -> account.withdrawWithFee(0, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Amount must be positive");
  }

  @Test
  void withdrawWithFee_overLimit_throws() {
    CheckingAccount account = accountWith(1_000_000);

    assertThatThrownBy(() -> account.withdrawWithFee(50_000_001, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Amount exceeds limit of 50000000");
  }

  @Test
  void withdraw_delegatesWithZeroFee() {
    CheckingAccount account = accountWith(1_000_000);

    account.withdraw(300_000);

    assertThat(account.getBalance()).isEqualTo(700_000);
  }
}
