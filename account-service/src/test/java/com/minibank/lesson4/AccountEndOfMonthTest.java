package com.minibank.lesson4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class AccountEndOfMonthTest {

  private final Customer customer = new Customer("a@minibank.com", "A", "0901000001");

  @Test
  void savings_endOfMonth_addsInterest() {
    SavingsAccount account = new SavingsAccount("SAV-1", customer, 50);
    account.deposit(10_000_000);

    account.endOfMonth();

    assertThat(account.getBalance()).isEqualTo(10_050_000);
  }

  @Test
  void savings_endOfMonth_zeroBalance_staysZero() {
    SavingsAccount account = new SavingsAccount("SAV-1", customer, 50);

    account.endOfMonth();

    assertThat(account.getBalance()).isZero();
  }

  @Test
  void checking_endOfMonth_deductsFee() {
    CheckingAccount account = new CheckingAccount("CHK-1", customer, 20_000);
    account.deposit(1_000_000);

    account.endOfMonth();

    assertThat(account.getBalance()).isEqualTo(980_000);
  }

  @Test
  void business_endOfMonth_deductsFee() {
    BusinessAccount account = new BusinessAccount("BIZ-1", customer, 100_000);
    account.deposit(5_000_000);

    account.endOfMonth();

    assertThat(account.getBalance()).isEqualTo(4_900_000);
  }

  @Test
  void business_endOfMonth_insufficientBalance_throws() {
    BusinessAccount account = new BusinessAccount("BIZ-1", customer, 100_000);

    assertThatThrownBy(account::endOfMonth)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Insufficient balance");
  }

  @Test
  void student_endOfMonth_doesNothing() {
    StudentAccount account = new StudentAccount("STU-1", customer);
    account.deposit(500_000);

    account.endOfMonth();

    assertThat(account.getBalance()).isEqualTo(500_000);
  }
}
