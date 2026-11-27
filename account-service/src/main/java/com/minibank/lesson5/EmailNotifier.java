package com.minibank.lesson5;

public class EmailNotifier implements Notifier {

  @Override
  public void send(Customer customer, String message) {
    System.out.println("📧 To " + customer.getEmail() + ": " + message);
  }
}
