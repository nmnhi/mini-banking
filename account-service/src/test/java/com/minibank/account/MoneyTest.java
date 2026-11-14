package com.minibank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

// Test do Claude tự sinh theo quy ước; sẽ ôn lại sau khi xong các session
class MoneyTest {

  @Test
  void constructor_negativeAmount_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(-1, "VND"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must not be negative");
  }

  @Test
  void constructor_blankCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(100, " "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must not be blank");
  }

  @Test
  void constructor_nullCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(100, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must not be blank");
  }

  @Test
  void constructor_zeroAmount_isAllowed() {
    assertThat(new Money(0, "VND").amount()).isZero();
  }

  @Test
  void add_sameCurrency_returnsNewMoneyAndKeepsOriginal() {
    Money original = new Money(100, "VND");

    Money result = original.add(new Money(50, "VND"));

    assertThat(result).isEqualTo(new Money(150, "VND"));
    assertThat(original).isEqualTo(new Money(100, "VND")); // object gốc không đổi
  }

  @Test
  void add_differentCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(100, "VND").add(new Money(1, "USD")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must match");
  }

  @Test
  void subtract_sameCurrency_returnsDifference() {
    assertThat(new Money(100, "VND").subtract(new Money(30, "VND")))
        .isEqualTo(new Money(70, "VND"));
  }

  @Test
  void subtract_resultNegative_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(100, "VND").subtract(new Money(101, "VND")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot subtract");
  }

  @Test
  void subtract_differentCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(100, "VND").subtract(new Money(1, "USD")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must match");
  }

  @Test
  void equals_sameValues_isEqualWithSameHashCode() {
    Money a = new Money(100, "VND");
    Money b = new Money(100, "VND");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void equals_differentAmountOrCurrency_isNotEqual() {
    Money base = new Money(100, "VND");

    assertThat(base).isNotEqualTo(new Money(200, "VND"));
    assertThat(base).isNotEqualTo(new Money(100, "USD"));
    assertThat(base).isNotEqualTo(null);
  }

  @Test
  void hashSet_equalMoneys_keepsOnlyOne() {
    Set<Money> set = new HashSet<>();
    set.add(new Money(100, "VND"));
    set.add(new Money(100, "VND"));

    assertThat(set).hasSize(1);
  }

  @Test
  void toString_returnsAmountAndCurrency() {
    assertThat(new Money(100, "VND")).hasToString("100 VND");
  }

  @Test
  void isLessThan_comparesAmountWithSameCurrency() {
    assertThat(new Money(30, "VND").isLessThan(new Money(100, "VND"))).isTrue();
    assertThat(new Money(100, "VND").isLessThan(new Money(100, "VND"))).isFalse();
  }

  @Test
  void isLessThan_differentCurrency_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> new Money(30, "VND").isLessThan(new Money(100, "USD")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency must match");
  }
}
