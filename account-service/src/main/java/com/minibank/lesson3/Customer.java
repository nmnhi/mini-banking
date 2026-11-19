package com.minibank.lesson3;

public class Customer {
  private final String email;
  private String name;

  public Customer(String email, String name) {
    requireValidEmail(email);
    requireValidName(name);
    this.email = email;
    this.name = name;
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

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public void updateName(String name) {
    requireValidName(name);
    this.name = name;
  }

}
