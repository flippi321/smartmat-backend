package edu.ntnu.idatt2106_09.backend.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * A REST controller for handling user authentication and registration. This controller provides endpoints for user
 * registration, authentication, and token refresh operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user by creating an account with the provided user details.
     * Sets access and refresh tokens as cookies in the HTTP response.
     *
     * @param registrationRequest The registration request containing user details such as first name, last name, email,
     *                            and password.
     * @param response            The HTTP response, containing access and refresh tokens as cookies.
     * @return An authentication response containing the registered user's information (ID, first name, last name, and
     * email).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
        log.debug("[X] Received registration request for email: {}", registrationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.register(registrationRequest, response);
        log.info("[X] Registration request successful for email: {}", registrationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }

    /**
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        log.debug("[X] Received registration request for email: {}", registrationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.register(registrationRequest);
        log.info("[X] Registration request successful for email: {}", registrationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }
    */

    /**
     * Authenticates a user by verifying the provided credentials.
     * Sets access and refresh tokens as cookies in the HTTP response.
     *
     * @param authenticationRequest The authentication request containing user credentials (email and password).
     * @param response              The HTTP response, containing access and refresh tokens as cookies.
     * @return An authentication response containing the authenticated user's information (ID, first name, last name,
     * and email).
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest, response);
        log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }

    /**
     @PostMapping("/authenticate")
     public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
     log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
     AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
     log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
     return ResponseEntity.ok(authenticationResponse);
     }
     */

    /**
     * Refreshes a user's access token using the provided refresh token.
     * Updates the access token cookie in the HTTP response.
     *
     * @param request  The HTTP request, containing the refresh token in the Authorization header.
     * @param response The HTTP response, containing the updated access token as a cookie.
     * @throws IOException If an error occurs while writing the authentication response to the output stream.
     */
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("[X] Received token refresh request");
        authenticationService.handleTokenRefreshRequest(request, response);
        log.info("[X] Token refresh request successful");
    }

    /**
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.handleTokenRefreshRequest(request, response);
    }
    */

}