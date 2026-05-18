package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest {

    private final Greeter greeter = new Greeter();

    @Test
    void greetsNamedPerson() {
        assertEquals("Hello, Alice!", greeter.greet("Alice"));
    }

    @Test
    void greetsStrangerWhenNameIsBlank() {
        assertEquals("Hello, stranger!", greeter.greet("  "));
    }

    @Test
    void greetsStrangerWhenNameIsNull() {
        assertEquals("Hello, stranger!", greeter.greet(null));
    }
}
