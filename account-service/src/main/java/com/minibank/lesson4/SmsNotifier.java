package com.minibank.lesson4;

public class SmsNotifier implements Notifier {

  @Override
  public void send(Customer customer, String message) {
    System.out.println("📱 To " + customer.getPhone() + ": " + message);
  }
}
