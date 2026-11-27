package com.minibank.lesson5;

public class Main {

  public static void main(String[] args) {
    Customer alice = new Customer("alice@minibank.com", "Alice", "0901000001");
    Customer bob = new Customer("bob@minibank.com", "Bob", "0901000002");
    Customer acme = new Customer("finance@acme.vn", "ACME Corp", "0281000003");
    Customer lan = new Customer("lan@student.edu.vn", "Lan", "0901000004");

    SavingsAccount aliceSav = new SavingsAccount("SAV-001", alice, 50);
    CheckingAccount bobChk = new CheckingAccount("CHK-001", bob, 20_000);
    BusinessAccount acmeBiz = new BusinessAccount("BIZ-001", acme, 100_000);
    StudentAccount lanStu = new StudentAccount("STU-001", lan);

    aliceSav.deposit(10_000_000);
    bobChk.deposit(1_000_000);
    acmeBiz.deposit(50_000_000);
    lanStu.deposit(500_000);

    TransferMessageFormatter formatter = new TransferMessageFormatter();
    AuditLogger auditLogger = new AuditLogger();
    TransferService emailService = new TransferService(new EmailNotifier(), formatter, auditLogger);
    TransferService smsService = new TransferService(new SmsNotifier(), formatter, auditLogger);

    // 1. Same TransferService, fee depends only on the injected policy
    System.out.println("=== 1. Successful transfers with different fee policies ===");
    transfer("INTERNAL 1,000,000 SAV-001 -> CHK-001 (fee 0)",
        emailService, aliceSav, bobChk, 1_000_000, new InternalFeePolicy(),
        9_000_000, 2_000_000);
    transfer("NAPAS 500,000 SAV-001 -> CHK-001 (fee 3,300)",
        emailService, aliceSav, bobChk, 500_000, new NapasFeePolicy(),
        8_496_700, 2_500_000);
    transfer("SWIFT 30,000,000 BIZ-001 -> SAV-001 (fee max(200,000, 0.1%) = 200,000)",
        smsService, acmeBiz, aliceSav, 30_000_000, new SwiftFeePolicy(),
        19_800_000, 38_496_700);

    // 2. Rejected transfers: balances must stay unchanged, audit logs FAILED, no notification
    System.out.println();
    System.out.println("=== 2. Rejected transfers (balances unchanged, no notification) ===");
    transfer("NAPAS 499,000 STU-001 -> CHK-001 (enough for amount, not for amount + fee)",
        smsService, lanStu, bobChk, 499_000, new NapasFeePolicy(),
        500_000, 2_500_000);
    transfer("INTERNAL 60,000,000 BIZ-001 -> CHK-001 (exceeds withdrawal limit)",
        emailService, acmeBiz, bobChk, 60_000_000, new InternalFeePolicy(),
        19_800_000, 2_500_000);
    transfer("INTERNAL 0 SAV-001 -> CHK-001 (amount must be positive)",
        emailService, aliceSav, bobChk, 0, new InternalFeePolicy(),
        38_496_700, 2_500_000);
    transfer("INTERNAL 100,000 CHK-001 -> CHK-001 (same account)",
        emailService, bobChk, bobChk, 100_000, new InternalFeePolicy(),
        2_500_000, 2_500_000);
    transfer("INTERNAL 100,000 CHK-001 -> null (missing receiver)",
        emailService, bobChk, null, 100_000, new InternalFeePolicy(),
        2_500_000, 0);
    transfer("null policy 100,000 CHK-001 -> SAV-001 (missing fee policy)",
        emailService, bobChk, aliceSav, 100_000, null,
        2_500_000, 38_496_700);

    // 3. Open/Closed: a new fee policy plugs in without touching TransferService
    System.out.println();
    System.out.println("=== 3. New VIP policy added without changing TransferService ===");
    TransferFeePolicy vipPolicy = new TransferFeePolicy() {
      @Override
      public long calculate(long amount) {
        return 1_000;
      }

      @Override
      public String type() {
        return "VIP";
      }
    };
    transfer("VIP 1,000,000 CHK-001 -> STU-001 (flat fee 1,000)",
        smsService, bobChk, lanStu, 1_000_000, vipPolicy,
        1_499_000, 1_500_000);
  }

  private static void transfer(String scenario, TransferService service, Account from, Account to,
      long amount, TransferFeePolicy policy, long expectedFrom, long expectedTo) {
    System.out.println("--- " + scenario);
    long beforeFrom = balanceOf(from);
    long beforeTo = balanceOf(to);

    String result;
    try {
      service.transfer(from, to, amount, policy);
      result = "OK";
    } catch (RuntimeException e) {
      result = "REJECTED: " + e.getMessage();
    }

    long afterFrom = balanceOf(from);
    long afterTo = balanceOf(to);
    boolean pass = afterFrom == expectedFrom && afterTo == expectedTo;

    System.out.println("    result: " + result);
    System.out.printf("    from %-8s before=%,12d  after=%,12d  expected=%,12d%n",
        idOf(from), beforeFrom, afterFrom, expectedFrom);
    System.out.printf("    to   %-8s before=%,12d  after=%,12d  expected=%,12d%n",
        idOf(to), beforeTo, afterTo, expectedTo);
    System.out.println("    " + (pass ? "✅ PASS" : "❌ FAIL"));
  }

  private static long balanceOf(Account account) {
    return account == null ? 0 : account.getBalance();
  }

  private static String idOf(Account account) {
    return account == null ? "-" : account.getId();
  }
}
