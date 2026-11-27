package com.minibank.lesson5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class AuditLoggerTest {

  private static final Instant TIME = Instant.parse("2026-11-27T10:00:00Z");

  private final AuditLogger logger = new AuditLogger();
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

  private String printed() {
    return out.toString(StandardCharsets.UTF_8).trim();
  }

  @Test
  void logSuccess_printsAllFieldsWithSuccessStatus() {
    logger.logSuccess("tx-1", TIME, "NAPAS", "SAV-1", "CHK-1", 500_000, 3_300);

    assertThat(printed()).isEqualTo(
        "[AUDIT] txId=tx-1 time=2026-11-27T10:00:00Z type=NAPAS from=SAV-1 to=CHK-1"
            + " amount=500000 fee=3300 status=SUCCESS");
  }

  @Test
  void logFailed_withFee_printsFeeAndReason() {
    logger.logFailed("tx-2", TIME, "NAPAS", "STU-1", "CHK-1", 499_000, 3_300L, "Insufficient balance");

    assertThat(printed()).isEqualTo(
        "[AUDIT] txId=tx-2 time=2026-11-27T10:00:00Z type=NAPAS from=STU-1 to=CHK-1"
            + " amount=499000 fee=3300 status=FAILED reason=\"Insufficient balance\"");
  }

  @Test
  void logFailed_nullFee_printsDash() {
    logger.logFailed("tx-3", TIME, "INTERNAL", "SAV-1", "CHK-1", 0, null, "Amount must be positive");

    assertThat(printed()).contains("fee=- status=FAILED");
  }
}
