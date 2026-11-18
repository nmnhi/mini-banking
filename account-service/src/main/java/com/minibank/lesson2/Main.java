package com.minibank.lesson2;

public class Main {
  public static void main(String[] args) {
    // 1. Tiêu vượt hạn mức
    DebitCard card1 = new DebitCard("1111", 100);
    card1.spend(80);
    attempt("1. Spend 30 when 80/100 already spent", () -> card1.spend(30));

    // 2. Tiêu khi thẻ đang bị khoá
    DebitCard card2 = new DebitCard("2222", 100);
    card2.lock();
    attempt("2. Spend 10 on a locked card", () -> card2.spend(10));
    card2.unlock();
    attempt("   Spend 10 after unlock", () -> card2.spend(10));

    // 3. Đổi hạn mức xuống thấp hơn số đã tiêu
    DebitCard card3 = new DebitCard("3333", 100);
    card3.spend(70);
    attempt("3. Lower limit to 50 when 70 already spent", () -> card3.setDailyLimit(50));

    // 4. Reset xong thì tiêu tiếp được
    DebitCard card4 = new DebitCard("4444", 100);
    card4.spend(100);
    attempt("4. Spend 1 after hitting the limit", () -> card4.spend(1));
    card4.resetDailySpending();
    attempt("   Spend 100 after resetDailySpending", () -> card4.spend(100));
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
