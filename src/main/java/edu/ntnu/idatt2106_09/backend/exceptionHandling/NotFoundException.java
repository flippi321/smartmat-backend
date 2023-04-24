package edu.ntnu.idatt2106_09.backend.exceptionHandling;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
