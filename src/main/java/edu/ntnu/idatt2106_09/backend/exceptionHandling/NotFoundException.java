package edu.ntnu.idatt2106_09.backend.exceptionHandling;

/**
 * This exception is thrown when a resource is not found. This is typically used when the client requests a resource
 * that does not exist. The exception handler will return a 404 Not Found response to the client.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified detail message.
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public NotFoundException(String message) {
        super(message);
    }
}
