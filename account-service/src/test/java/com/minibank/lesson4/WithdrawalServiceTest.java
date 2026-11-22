package com.minibank.lesson4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class WithdrawalServiceTest {

  /** Fake notifier: records calls instead of printing. Possible only because Notifier is an interface. */
  private static class RecordingNotifier implements Notifier {
    final List<Customer> customers = new ArrayList<>();
    final List<String> messages = new ArrayList<>();

    @Override
    public void send(Customer customer, String message) {
      customers.add(customer);
      messages.add(message);
    }
  }

  private final Customer customer = new Customer("a@minibank.com", "A", "0901000001");
  private final RecordingNotifier notifier = new RecordingNotifier();
  private final WithdrawalService service = new WithdrawalService(notifier);

  @Test
  void constructor_nullNotifier_throws() {
    assertThatThrownBy(() -> new WithdrawalService(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void withdraw_success_notifiesAccountOwner() {
    CheckingAccount account = new CheckingAccount("CHK-1", customer, 0);
    account.deposit(1_000_000);

    service.withdraw(account, 200_000);

    assertThat(account.getBalance()).isEqualTo(800_000);
    assertThat(notifier.customers).containsExactly(customer);
    assertThat(notifier.messages).containsExactly("You have withdrawn 200000 from account CHK-1");
  }

  @Test
  void withdraw_failure_sendsNothing() {
    CheckingAccount account = new CheckingAccount("CHK-1", customer, 0);

    assertThatThrownBy(() -> service.withdraw(account, 100_000))
        .isInstanceOf(IllegalStateException.class);
    assertThat(notifier.customers).isEmpty();
  }
}
