package edu.ntnu.idatt2106_09.backend.exceptionHandling;

/**
 * This exception is thrown when a bad request is made by the client. This is typically used when there is something
 * wrong with the request that the client made, for example, missing or invalid parameters.
 * The exception handler will return a 400 Bad Request response to the client.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public BadRequestException(String message) {
        super(message);
    }
}