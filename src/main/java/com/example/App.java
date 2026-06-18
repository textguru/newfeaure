package com.example;

/**
 * Application entry point for the sample Java project.
 */
public class App {

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Greeter greeter = new Greeter();

        System.out.println(greeter.greet("World"));
        System.out.println("2 + 3 = " + calculator.add(2, 3));
        System.out.println("10 - 4 = " + calculator.subtract(10, 4));
        System.out.println("6 * 7 = " + calculator.multiply(6, 7));
        System.out.println("20 / 5 = " + calculator.divide(20, 5));
    }
}
