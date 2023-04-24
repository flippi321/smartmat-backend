package edu.ntnu.idatt2106_09.backend.exceptionHandling;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}