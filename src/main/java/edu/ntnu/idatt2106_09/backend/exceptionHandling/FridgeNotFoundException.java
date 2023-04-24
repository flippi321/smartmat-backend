package edu.ntnu.idatt2106_09.backend.exceptionHandling;

public class FridgeNotFoundException extends RuntimeException {

    public FridgeNotFoundException(String message) {
        super(message);
    }
}