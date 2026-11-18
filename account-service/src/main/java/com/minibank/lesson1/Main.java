package com.minibank.lesson1;

public class Main {
  public static void main(String[] args) {
    Wallet nam = new Wallet("Nam", 0);
    Wallet lan = new Wallet("Lan", 0);

    nam.deposit(500);
    nam.pay(200);

    lan.deposit(100);

    printBalance(nam);
    printBalance(lan);

    lan.pay(1000);
  }

  private static void printBalance(Wallet wallet) {
    System.out.println(wallet.getOwnerName() + ": " + wallet.getBalance());
  }
}
