package edu.ntnu.idatt2106_09.backend.exceptionHandling;

/**
 * This exception is thrown when an internal server error occurs. This is typically used when something goes wrong on
 * the server that is not the client's fault.
 * The exception handler will return a 500 Internal Server Error response to the client.
 */
public class InternalServerErrorException extends RuntimeException {

    /**
     * Constructs a new InternalServerErrorException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public InternalServerErrorException(String message) {
        super(message);
    }
}