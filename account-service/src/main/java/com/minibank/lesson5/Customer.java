package com.minibank.lesson5;

public class Customer {

  private final String email;
  private final String phone;
  private String name;

  public Customer(String email, String name, String phone) {
    requireValidEmail(email);
    requireValidName(name);
    requireValidPhone(phone);
    this.email = email;
    this.name = name;
    this.phone = phone;
  }

  private void requireValidEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be null or blank");
    }
  }

  private void requireValidName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
  }

  private void requireValidPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      throw new IllegalArgumentException("Phone cannot be null or blank");
    }
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPhone() {
    return phone;
  }

  public void updateName(String name) {
    requireValidName(name);
    this.name = name;
  }

}
