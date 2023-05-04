package edu.ntnu.idatt2106_09.backend.exceptionHandling;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}