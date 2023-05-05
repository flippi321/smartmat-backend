package edu.ntnu.idatt2106_09.backend.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A global exception handler class that handles exceptions thrown by the application controllers and returns
 * a ResponseEntity object with an appropriate HTTP status code and message.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles NotFoundException and returns a ResponseEntity object with HTTP status code NOT_FOUND (404) and the
     * exception message as the response body.
     *
     * @param ex the NotFoundException to handle.
     * @return a ResponseEntity object with HTTP status code NOT_FOUND (404) and the exception message as the response
     * body.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BadRequestException and returns a ResponseEntity object with HTTP status code BAD_REQUEST (400) and the
     * exception message as the response body.
     *
     * @param ex the BadRequestException to handle.
     * @return a ResponseEntity object with HTTP status code BAD_REQUEST (400) and the exception message as the response
     * body.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles BadCredentialsException and returns a ResponseEntity object with HTTP status code UNAUTHORIZED (401) and the
     * exception message as the response body.
     *
     * @param ex the BadCredentialsException to handle.
     * @return a ResponseEntity object with HTTP status code UNAUTHORIZED (401) and the exception message as the
     * response body.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles UsernameNotFoundException and returns a ResponseEntity object with HTTP status code NOT_FOUND (404) and
     * the exception message as the response body.
     *
     * @param ex the UsernameNotFoundException to handle.
     * @return a ResponseEntity object with HTTP status code NOT_FOUND (404) and the exception message as the response
     * body.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InternalServerErrorException and returns a ResponseEntity object with HTTP status code
     * INTERNAL_SERVER_ERROR (500) and the exception message as the response body.
     *
     * @param ex the InternalServerErrorException to handle.
     * @return a ResponseEntity object with HTTP status code INTERNAL_SERVER_ERROR (500) and the exception message as
     * the response body.
     */
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}