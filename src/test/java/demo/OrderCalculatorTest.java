package demo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCalculatorTest {
    private final OrderCalculator calculator = new OrderCalculator();

    @Test
    void appliesPercentageDiscount() {
        assertEquals(new BigDecimal("102.00"),
                calculator.totalAfterDiscount(new BigDecimal("120.00"), new BigDecimal("15")));
    }

    @Test
    void zeroDiscountKeepsSubtotal() {
        assertEquals(new BigDecimal("19.99"),
                calculator.totalAfterDiscount(new BigDecimal("19.99"), BigDecimal.ZERO));
    }

    @Test
    void fullDiscountProducesZero() {
        assertEquals(new BigDecimal("0.00"),
                calculator.totalAfterDiscount(new BigDecimal("19.99"), new BigDecimal("100")));
    }

    @Test
    void rejectsDiscountAboveOneHundredPercent() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.totalAfterDiscount(new BigDecimal("20"), new BigDecimal("101")));
    }

    @Test
    void rejectsNegativeSubtotal() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.totalAfterDiscount(new BigDecimal("-1"), BigDecimal.ZERO));
    }

    @Test
    void roundsFinalAmountToTwoDecimalPlaces() {
        assertEquals(new BigDecimal("9.05"),
                calculator.totalAfterDiscount(new BigDecimal("10.05"), new BigDecimal("10")));
    }

    @Test
    void rejectsMissingSubtotal() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.totalAfterDiscount(null, BigDecimal.ZERO));
    }
}
