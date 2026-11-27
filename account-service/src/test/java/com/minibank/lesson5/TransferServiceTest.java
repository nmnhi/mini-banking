package com.minibank.lesson5;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class TransferServiceTest {

  /** Fake notifier: records calls instead of printing. */
  private static class RecordingNotifier implements Notifier {
    final List<Customer> customers = new ArrayList<>();
    final List<String> messages = new ArrayList<>();

    @Override
    public void send(Customer customer, String message) {
      customers.add(customer);
      messages.add(message);
    }
  }

  /** Fake notifier whose channel is down. */
  private static class FailingNotifier implements Notifier {
    @Override
    public void send(Customer customer, String message) {
      throw new IllegalStateException("SMS gateway down");
    }
  }

  /** Fake audit logger: records each entry as "STATUS type from to fee reason" instead of printing. */
  private static class RecordingAuditLogger extends AuditLogger {
    final List<String> entries = new ArrayList<>();
    final List<String> txIds = new ArrayList<>();

    @Override
    public void logSuccess(String txId, Instant timestamp, String type, String fromId, String toId,
        long amount, long fee) {
      txIds.add(txId);
      entries.add("SUCCESS " + type + " " + fromId + " " + toId + " " + amount + " " + fee);
    }

    @Override
    public void logFailed(String txId, Instant timestamp, String type, String fromId, String toId,
        long amount, Long fee, String reason) {
      txIds.add(txId);
      entries.add("FAILED " + type + " " + fromId + " " + toId + " " + amount + " " + fee + " " + reason);
    }
  }

  private final Customer alice = new Customer("alice@minibank.com", "Alice", "0901000001");
  private final Customer bob = new Customer("bob@minibank.com", "Bob", "0901000002");
  private final RecordingNotifier notifier = new RecordingNotifier();
  private final RecordingAuditLogger audit = new RecordingAuditLogger();
  private final TransferMessageFormatter formatter = new TransferMessageFormatter();
  private final TransferService service = new TransferService(notifier, formatter, audit);

  private Account accountOf(String id, Customer owner, long balance) {
    CheckingAccount account = new CheckingAccount(id, owner, 0);
    if (balance > 0) {
      account.deposit(balance);
    }
    return account;
  }

  // ---- constructor ----

  @Test
  void constructor_nullDependencies_throw() {
    assertThatThrownBy(() -> new TransferService(null, formatter, audit))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new TransferService(notifier, null, audit))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new TransferService(notifier, formatter, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // ---- success ----

  @Test
  void transfer_success_movesMoneyAndChargesFeeToSender() {
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 100_000);

    service.transfer(from, to, 500_000, new NapasFeePolicy());

    assertThat(from.getBalance()).isEqualTo(496_700);
    assertThat(to.getBalance()).isEqualTo(600_000);
  }

  @Test
  void transfer_success_logsSuccessOnce() {
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 0);

    service.transfer(from, to, 500_000, new NapasFeePolicy());

    assertThat(audit.entries).containsExactly("SUCCESS NAPAS A-1 B-1 500000 3300");
  }

  @Test
  void transfer_success_notifiesSenderThenReceiverWithFormatterMessages() {
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 0);

    service.transfer(from, to, 500_000, new NapasFeePolicy());

    assertThat(notifier.customers).containsExactly(alice, bob);
    assertThat(notifier.messages).containsExactly(
        formatter.forSender(500_000, 3_300, "B-1", 496_700),
        formatter.forReceiver(500_000, "A-1"));
  }

  @Test
  void transfer_newPolicy_worksWithoutChangingService() {
    TransferFeePolicy vip = new TransferFeePolicy() {
      @Override
      public long calculate(long amount) {
        return 1_000;
      }

      @Override
      public String type() {
        return "VIP";
      }
    };
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 0);

    service.transfer(from, to, 100_000, vip);

    assertThat(from.getBalance()).isEqualTo(899_000);
    assertThat(audit.entries).containsExactly("SUCCESS VIP A-1 B-1 100000 1000");
  }

  @Test
  void transfer_eachCallGetsNewTxId() {
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 0);

    service.transfer(from, to, 100_000, new InternalFeePolicy());
    service.transfer(from, to, 100_000, new InternalFeePolicy());

    assertThat(audit.txIds).hasSize(2).doesNotHaveDuplicates();
  }

  // ---- rejected ----

  @Test
  void transfer_insufficientForFee_keepsOriginalExceptionAndBalances() {
    Account from = accountOf("A-1", alice, 500_000);
    Account to = accountOf("B-1", bob, 0);

    assertThatThrownBy(() -> service.transfer(from, to, 499_000, new NapasFeePolicy()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Insufficient balance");

    assertThat(from.getBalance()).isEqualTo(500_000);
    assertThat(to.getBalance()).isZero();
    assertThat(audit.entries).containsExactly("FAILED NAPAS A-1 B-1 499000 3300 Insufficient balance");
    assertThat(notifier.messages).isEmpty();
  }

  @Test
  void transfer_nonPositiveAmount_rejectedBeforeFeeIsCalculated() {
    Account from = accountOf("A-1", alice, 500_000);
    Account to = accountOf("B-1", bob, 0);

    assertThatThrownBy(() -> service.transfer(from, to, 0, new NapasFeePolicy()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Amount must be positive");

    assertThat(audit.entries).containsExactly("FAILED NAPAS A-1 B-1 0 null Amount must be positive");
    assertThat(notifier.messages).isEmpty();
  }

  @Test
  void transfer_sameAccountObject_rejected() {
    Account account = accountOf("A-1", alice, 500_000);

    assertThatThrownBy(() -> service.transfer(account, account, 100_000, new InternalFeePolicy()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sender and Receiver cannot be same");
    assertThat(account.getBalance()).isEqualTo(500_000);
  }

  @Test
  void transfer_differentObjectsWithSameId_rejected() {
    Account from = accountOf("A-1", alice, 500_000);
    Account sameIdCopy = accountOf("A-1", alice, 0);

    assertThatThrownBy(() -> service.transfer(from, sameIdCopy, 100_000, new InternalFeePolicy()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sender and Receiver cannot be same");
  }

  @Test
  void transfer_nullSender_throwsValidationErrorNotNpe() {
    Account to = accountOf("B-1", bob, 0);

    assertThatThrownBy(() -> service.transfer(null, to, 100_000, new InternalFeePolicy()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sender cannot be null");
    assertThat(audit.entries).containsExactly("FAILED INTERNAL - B-1 100000 null Sender cannot be null");
  }

  @Test
  void transfer_nullReceiver_throwsValidationErrorNotNpe() {
    Account from = accountOf("A-1", alice, 500_000);

    assertThatThrownBy(() -> service.transfer(from, null, 100_000, new InternalFeePolicy()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Receiver cannot be null");
    assertThat(from.getBalance()).isEqualTo(500_000);
    assertThat(audit.entries).containsExactly("FAILED INTERNAL A-1 - 100000 null Receiver cannot be null");
  }

  @Test
  void transfer_nullPolicy_throwsValidationErrorNotNpe() {
    Account from = accountOf("A-1", alice, 500_000);
    Account to = accountOf("B-1", bob, 0);

    assertThatThrownBy(() -> service.transfer(from, to, 100_000, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FeePolicy cannot be null");
    assertThat(audit.entries).containsExactly("FAILED - A-1 B-1 100000 null FeePolicy cannot be null");
  }

  // ---- notification failure ----

  @Test
  void transfer_notifierFails_moneyStillMovedAndNoFailedLog() {
    TransferService failingService = new TransferService(new FailingNotifier(), formatter, audit);
    Account from = accountOf("A-1", alice, 1_000_000);
    Account to = accountOf("B-1", bob, 0);

    assertThatThrownBy(() -> failingService.transfer(from, to, 100_000, new InternalFeePolicy()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("SMS gateway down");

    assertThat(from.getBalance()).isEqualTo(900_000);
    assertThat(to.getBalance()).isEqualTo(100_000);
    assertThat(audit.entries).containsExactly("SUCCESS INTERNAL A-1 B-1 100000 0");
  }
}
