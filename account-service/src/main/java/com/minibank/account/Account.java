package com.minibank.account;

import java.util.ArrayList;
import java.util.List;

public class Account {

  private final String id;
  private final List<Transaction> transactions;
  private Money balance;

  public Account(String id, String currency) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id must not be blank");
    }

    this.id = id;
    this.balance = new Money(0, currency);
    this.transactions = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public Money getBalance() {
    return balance;
  }

  public void deposit(Money amount) {
    if (amount.amount() <= 0) {
      throw new IllegalArgumentException("Amount must be greater than 0, but was " + amount);
    }

    balance = balance.add(amount);
    transactions.add(Transaction.of(TransactionType.DEPOSIT, amount)); // ghi số tiền giao dịch, không phải số dư
  }

  public void withdraw(Money amount) {
    if (amount.amount() <= 0) {
      throw new IllegalArgumentException("Amount must be greater than 0, but was " + amount);
    }

    // Kiểm tra trước khi trừ; isLessThan cũng chặn luôn trường hợp khác loại tiền
    if (balance.isLessThan(amount)) {
      throw new InsufficientBalanceException("Insufficient balance: balance= " + balance +
          ", " + "requested= " + amount);
    }

    balance = balance.subtract(amount);
    transactions.add(Transaction.of(TransactionType.WITHDRAW, amount)); // ghi số tiền giao dịch, không phải số dư
  }

  public List<Transaction> getTransactions() {
    return List.copyOf(transactions); // snapshot: bên ngoài không sửa được, không bị ảnh hưởng về sau
  }

  // Account là entity nên chỉ so sánh theo id; balance thay đổi không làm đổi danh tính
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account other = (Account) o;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}
