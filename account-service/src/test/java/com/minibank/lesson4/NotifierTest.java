package com.minibank.lesson4;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class NotifierTest {

  private final Customer customer = new Customer("a@minibank.com", "A", "0901000001");
  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private PrintStream originalOut;

  @BeforeEach
  void captureStdout() {
    originalOut = System.out;
    System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
  }

  @AfterEach
  void restoreStdout() {
    System.setOut(originalOut);
  }

  @Test
  void email_sendsToEmailAddress() {
    new EmailNotifier().send(customer, "hi");

    String printed = out.toString(StandardCharsets.UTF_8);
    assertThat(printed).contains("a@minibank.com").contains("hi").doesNotContain("0901000001");
  }

  @Test
  void sms_sendsToPhoneNumber() {
    new SmsNotifier().send(customer, "hi");

    String printed = out.toString(StandardCharsets.UTF_8);
    assertThat(printed).contains("0901000001").contains("hi").doesNotContain("a@minibank.com");
  }
}
