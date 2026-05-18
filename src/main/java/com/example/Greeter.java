package com.example;

/**
 * Builds greeting messages.
 */
public class Greeter {

    public String greet(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, stranger!";
        }
        return "Hello, " + name + "!";
    }
}
