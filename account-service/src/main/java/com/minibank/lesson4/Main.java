package com.minibank.lesson4;

public class Main {

  public static void main(String[] args) {
    Customer alice = new Customer("alice@minibank.com", "Alice", "0901000001");
    Customer bob = new Customer("bob@minibank.com", "Bob", "0901000002");
    Customer acme = new Customer("finance@acme.vn", "ACME Corp", "0281000003");
    Customer lan = new Customer("lan@student.edu.vn", "Lan", "0901000004");

    // 1. Month-end job runs over all 4 account types — no instanceof anywhere
    System.out.println("=== 1. Month-end job on all 4 account types ===");
    SavingsAccount savings = new SavingsAccount("SAV-001", alice, 50);        // 0.5% / month
    CheckingAccount checking = new CheckingAccount("CHK-001", bob, 20_000);
    BusinessAccount business = new BusinessAccount("BIZ-001", acme, 100_000);
    StudentAccount student = new StudentAccount("STU-001", lan);

    savings.deposit(10_000_000);
    checking.deposit(1_000_000);
    business.deposit(5_000_000);
    student.deposit(500_000);

    Account[] accounts = {savings, checking, business, student};
    long[] expected = {10_050_000, 980_000, 4_900_000, 500_000};

    long[] before = new long[accounts.length];
    for (int i = 0; i < accounts.length; i++) {
      before[i] = accounts[i].getBalance();
    }

    new MonthEndJob().run(accounts);

    for (int i = 0; i < accounts.length; i++) {
      Account account = accounts[i];
      long after = account.getBalance();
      String status = after == expected[i] ? "✅" : "❌";
      System.out.printf("%s %-16s %-8s before=%,12d  after=%,12d  expected=%,12d%n",
          status, account.getClass().getSimpleName(), account.getId(),
          before[i], after, expected[i]);
    }

    // 2. Same WithdrawalService, different channel — the service only knows Notifier
    System.out.println();
    System.out.println("=== 2. WithdrawalService with Email vs SMS ===");
    WithdrawalService emailService = new WithdrawalService(new EmailNotifier());
    WithdrawalService smsService = new WithdrawalService(new SmsNotifier());

    attempt("Email service withdraw 1,000,000 from SAV-001 (expect 📧 to alice@minibank.com)",
        () -> emailService.withdraw(savings, 1_000_000));
    attempt("SMS service   withdraw 200,000 from CHK-001 (expect 📱 to 0901000002)",
        () -> smsService.withdraw(checking, 200_000));

    // 3. Failed withdrawal -> no notification on either channel
    System.out.println();
    System.out.println("=== 3. Failed withdrawal sends nothing ===");
    attempt("Email service withdraw 60,000,000 (expect NO email)",
        () -> emailService.withdraw(savings, 60_000_000));
    attempt("SMS service   withdraw 9,000,000 from CHK-001 (expect NO sms)",
        () -> smsService.withdraw(checking, 9_000_000));

    // 4. Customer without phone is rejected
    System.out.println();
    System.out.println("=== 4. Customer phone is required ===");
    attempt("new Customer with blank phone",
        () -> new Customer("x@minibank.com", "X", "  "));
    attempt("new Customer with null phone",
        () -> new Customer("x@minibank.com", "X", null));
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
