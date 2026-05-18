package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void subtractsTwoNumbers() {
        assertEquals(6, calculator.subtract(10, 4));
    }

    @Test
    void multipliesTwoNumbers() {
        assertEquals(42, calculator.multiply(6, 7));
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(4, calculator.divide(20, 5));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(1, 0));
    }
}
