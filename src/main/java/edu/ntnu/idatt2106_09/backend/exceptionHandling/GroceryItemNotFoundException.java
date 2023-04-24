package edu.ntnu.idatt2106_09.backend.exceptionHandling;

public class GroceryItemNotFoundException extends RuntimeException {

    public GroceryItemNotFoundException(String message) {
        super(message);
    }
}