package com.minibank.lesson4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class CustomerTest {

  @Test
  void constructor_validInput_storesAllFields() {
    Customer customer = new Customer("a@minibank.com", "Alice", "0901000001");

    assertThat(customer.getEmail()).isEqualTo("a@minibank.com");
    assertThat(customer.getName()).isEqualTo("Alice");
    assertThat(customer.getPhone()).isEqualTo("0901000001");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  "})
  void constructor_invalidPhone_throwsWithPhoneMessage(String phone) {
    assertThatThrownBy(() -> new Customer("a@minibank.com", "Alice", phone))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Phone");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  "})
  void constructor_invalidEmail_throws(String email) {
    assertThatThrownBy(() -> new Customer(email, "Alice", "0901000001"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email");
  }
}
