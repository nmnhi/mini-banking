package com.minibank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class AccountTest {

  private static Money vnd(long amount) {
    return new Money(amount, "VND");
  }

  @Test
  void newAccount_hasZeroBalanceAndNoTransactions() {
    Account account = new Account("ACC-1", "VND");

    assertThat(account.getBalance()).isEqualTo(vnd(0));
    assertThat(account.getTransactions()).isEmpty();
  }

  @Test
  void constructor_blankId_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Account(" ", "VND"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("id must not be blank");
  }

  @Test
  void constructor_nullId_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Account(null, "VND"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void constructor_blankCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Account("ACC-1", " "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deposit_positiveAmount_increasesBalance() {
    Account account = new Account("ACC-1", "VND");

    account.deposit(vnd(100));
    account.deposit(vnd(50));

    assertThat(account.getBalance()).isEqualTo(vnd(150));
  }

  @Test
  void deposit_zeroAmount_throwsIllegalArgumentException() {
    Account account = new Account("ACC-1", "VND");

    assertThatThrownBy(() -> account.deposit(vnd(0)))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(account.getTransactions()).isEmpty();
  }

  @Test
  void deposit_differentCurrency_throwsIllegalArgumentException() {
    Account account = new Account("ACC-1", "VND");

    assertThatThrownBy(() -> account.deposit(new Money(10, "USD")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must match");
  }

  @Test
  void withdraw_lessThanBalance_decreasesBalance() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(150));

    account.withdraw(vnd(30));

    assertThat(account.getBalance()).isEqualTo(vnd(120));
  }

  @Test
  void withdraw_exactBalance_leavesZero() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));

    account.withdraw(vnd(100));

    assertThat(account.getBalance()).isEqualTo(vnd(0));
  }

  @Test
  void withdraw_moreThanBalance_throwsInsufficientBalanceAndKeepsState() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));

    assertThatThrownBy(() -> account.withdraw(vnd(101)))
        .isInstanceOf(InsufficientBalanceException.class)
        .hasMessageContaining("Insufficient balance");
    assertThat(account.getBalance()).isEqualTo(vnd(100));
    assertThat(account.getTransactions()).hasSize(1);
  }

  @Test
  void withdraw_zeroAmount_throwsIllegalArgumentException() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));

    assertThatThrownBy(() -> account.withdraw(vnd(0)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void withdraw_differentCurrency_throwsIllegalArgumentException() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));

    assertThatThrownBy(() -> account.withdraw(new Money(10, "USD")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must match");
  }

  @Test
  void transactions_recordTypeAndTransactionAmount() {
    Account account = new Account("ACC-1", "VND");

    account.deposit(vnd(100));
    account.deposit(vnd(50));
    account.withdraw(vnd(30));

    List<Transaction> txs = account.getTransactions();
    assertThat(txs).extracting(Transaction::type)
        .containsExactly(TransactionType.DEPOSIT, TransactionType.DEPOSIT, TransactionType.WITHDRAW);
    assertThat(txs).extracting(Transaction::amount)
        .containsExactly(vnd(100), vnd(50), vnd(30));
  }

  @Test
  void getTransactions_cannotBeModifiedFromOutside() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));

    List<Transaction> txs = account.getTransactions();

    assertThatThrownBy(txs::clear).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getTransactions_isSnapshot() {
    Account account = new Account("ACC-1", "VND");
    account.deposit(vnd(100));
    List<Transaction> snapshot = account.getTransactions();

    account.deposit(vnd(50));

    assertThat(snapshot).hasSize(1);
  }

  @Test
  void equals_sameId_isEqualEvenWithDifferentBalance() {
    Account a = new Account("ACC-1", "VND");
    Account b = new Account("ACC-1", "VND");
    b.deposit(vnd(500));

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
    assertThat(a).isNotEqualTo(new Account("ACC-2", "VND"));
  }

  @Test
  void hashSet_accountStillFoundAfterBalanceChange() {
    Account account = new Account("ACC-1", "VND");
    Set<Account> set = new HashSet<>();
    set.add(account);

    account.deposit(vnd(100));

    assertThat(set).contains(account);
  }
}
