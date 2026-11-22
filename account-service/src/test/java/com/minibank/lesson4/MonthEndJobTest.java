package com.minibank.lesson4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class MonthEndJobTest {

  private final Customer customer = new Customer("a@minibank.com", "A", "0901000001");

  @Test
  void run_processesEveryAccountType() {
    SavingsAccount savings = new SavingsAccount("SAV-1", customer, 50);
    CheckingAccount checking = new CheckingAccount("CHK-1", customer, 20_000);
    BusinessAccount business = new BusinessAccount("BIZ-1", customer, 100_000);
    StudentAccount student = new StudentAccount("STU-1", customer);
    savings.deposit(10_000_000);
    checking.deposit(1_000_000);
    business.deposit(5_000_000);
    student.deposit(500_000);

    new MonthEndJob().run(new Account[] {savings, checking, business, student});

    assertThat(savings.getBalance()).isEqualTo(10_050_000);
    assertThat(checking.getBalance()).isEqualTo(980_000);
    assertThat(business.getBalance()).isEqualTo(4_900_000);
    assertThat(student.getBalance()).isEqualTo(500_000);
  }

  @Test
  void run_callsEndOfMonthOnUnknownSubclass_withoutChangingJob() {
    // A brand-new account type the job has never heard of
    int[] calls = {0};
    Account custom = new Account("NEW-1", customer) {
      @Override
      public void endOfMonth() {
        calls[0]++;
      }
    };

    new MonthEndJob().run(new Account[] {custom});

    assertThat(calls[0]).isEqualTo(1);
  }

  @Test
  void run_emptyArray_doesNothing() {
    new MonthEndJob().run(new Account[0]);
  }
}
