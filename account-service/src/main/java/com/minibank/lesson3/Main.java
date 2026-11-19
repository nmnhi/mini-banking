package com.minibank.lesson3;

public class Main {
  public static void main(String[] args) {
    Customer alice = new Customer("alice@minibank.com", "Alice");
    Customer bob = new Customer("bob@minibank.com", "Bob");

    // 1. Existing behavior is not broken
    System.out.println("=== 1. Old behavior still works ===");
    SavingsAccount savings = new SavingsAccount("SAV-001", alice, 50); // 0.5% per month
    savings.deposit(10_000_000);
    savings.addMonthlyInterest();
    System.out.println("Savings 10,000,000 + 0.5% interest -> balance = " + savings.getBalance()
        + " (expected 10050000)");

    CheckingAccount checking = new CheckingAccount("CHK-001", bob, 20_000);
    checking.deposit(1_000_000);
    checking.deductMonthlyFee();
    System.out.println("Checking 1,000,000 - 20,000 fee -> balance = " + checking.getBalance()
        + " (expected 980000)");

    // 2. The 50M rule applies to both account types
    System.out.println();
    System.out.println("=== 2. Withdraw 60M is rejected on both account types ===");
    savings.deposit(100_000_000);
    checking.deposit(100_000_000);
    attempt("Savings  withdraw 60,000,000", () -> savings.withdraw(60_000_000));
    attempt("Checking withdraw 60,000,000", () -> checking.withdraw(60_000_000));
    attempt("Savings  withdraw exactly 50,000,000 (boundary)", () -> savings.withdraw(50_000_000));

    // 3. Success -> email sent; failure -> no email
    System.out.println();
    System.out.println("=== 3. Email only on successful withdrawal ===");
    WithdrawalService service = new WithdrawalService(new Notifier());
    attempt("Service withdraw 1,000,000 from SAV-001 (expect an email above)",
        () -> service.withdraw(savings, 1_000_000));
    attempt("Service withdraw 60,000,000 from CHK-001 (expect NO email)",
        () -> service.withdraw(checking, 60_000_000));

    CheckingAccount empty = new CheckingAccount("CHK-002", bob, 0);
    attempt("Service withdraw 100,000 from empty CHK-002 (expect NO email)",
        () -> service.withdraw(empty, 100_000));
  }

  private static void attempt(String scenario, Runnable action) {
    try {
      action.run();
      System.out.println(scenario + " -> OK");
    } catch (RuntimeException e) {
      System.out.println(scenario + " -> REJECTED: " + e.getMessage());
    }
  }
}
