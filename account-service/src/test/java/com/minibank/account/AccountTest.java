package com.minibank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AccountTest {

  // Test trường hợp đúng: nạp tiền thì số dư tăng
  @Test
  void deposit_positiveAmount_increasesBalance() {
    //Given
    Account account = new Account("ACC-1");

    //When
    account.deposit(100);

    //Then
    assertThat(account.getBalance()).isEqualTo(100);
  }

  // Test trường hợp lỗi: nạp số âm thì phải throw exception
  @Test
  void deposit_negativeAmount_throwsIllegalArgumentException() {
    //Given
    Account account = new Account("ACC-1");

    // When
    assertThatThrownBy(
        () -> account.deposit(-5))
        .isInstanceOf(IllegalArgumentException.class
        );
  }

  @Test
  void newAccount_hasZeroBalance() {
    Account account = new Account("ACC-1");

    assertThat(account.getBalance()).isEqualTo(0);
  }

  @Test
  void withdraw_amountWithinBalance_decreasesBalance() {
    Account account = new Account("ACC-1");

    account.deposit(300);
    account.withdraw(100);

    assertThat(account.getBalance()).isEqualTo(200);
  }

  @Test
  void withdraw_moreThanBalance_throwsInsufficientBalanceException() {
    Account account = new Account("ACC-1");
    account.deposit(300);

    assertThatThrownBy(() -> account.withdraw(1000)).isInstanceOf(
        InsufficientBalanceException.class);

    assertThat(account.getBalance()).isEqualTo(300);

  }

  @Test
  void constructor_blankId_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Account(" ")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void withdraw_negativeAmount_throwsIllegalArgumentException() {
    Account account = new Account("ACC-1");

    assertThatThrownBy(
        () -> account.withdraw(-5)).
        isInstanceOf(IllegalArgumentException.class
        );
  }

  @Test
  void constructor_nullId_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Account(null)).isInstanceOf(IllegalArgumentException.class);
  }
}
