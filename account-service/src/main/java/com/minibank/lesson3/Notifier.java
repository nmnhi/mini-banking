package com.minibank.lesson3;

public class Notifier {
  public void send(String email, String message) {
    System.out.println("📧 To " + email + ": " + message);
  }
}
