package com.cyxoud.robots.exceptions;

/**
 * Represents incorrect number of arguments when modelling is called
 */
public class IllegalArgumentsNumberException extends RuntimeException {
    public IllegalArgumentsNumberException(String message) {
        super(message);
    }
}
