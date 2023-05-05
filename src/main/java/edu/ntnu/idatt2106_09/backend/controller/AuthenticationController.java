package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationRequest;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationResponse;
import edu.ntnu.idatt2106_09.backend.authentication.RegistrationRequest;
import edu.ntnu.idatt2106_09.backend.service.security.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and token refresh")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user by processing a registration request and returning an {@link AuthenticationResponse}
     * containing the user's information, access token, and refresh token. The registration process includes validation
     * of registration details and saving the user in the system.
     *
     * @param registrationRequest the {@link RegistrationRequest} object containing the user's registration details
     * @return a {@link ResponseEntity} containing the {@link AuthenticationResponse} with user information, access
     *         token, and refresh token upon successful registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        log.debug("[X] Received registration request for email: {}", registrationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.register(registrationRequest);
        log.info("[X] Registration request successful for email: {}", registrationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }

    /**
     * Registers a new user by creating an account with the provided user details.
     * Sets access and refresh tokens as cookies in the HTTP response.
     *
     * @param registrationRequest The registration request containing user details such as first name, last name, email,
     *                            and password.
     * @param response            The HTTP response, containing access and refresh tokens as cookies.
     * @return An authentication response containing the registered user's information (ID, first name, last name, and
     *         email).
     */
    /**
     @PostMapping("/register")
     public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
     log.debug("[X] Received registration request for email: {}", registrationRequest.getEmail());
     AuthenticationResponse authenticationResponse = authenticationService.register(registrationRequest, response);
     log.info("[X] Registration request successful for email: {}", registrationRequest.getEmail());
     return ResponseEntity.ok(authenticationResponse);
     }
     */

    /**
     * Authenticates a user by processing an authentication request and returning an {@link AuthenticationResponse}
     * containing the user's information, access token, and refresh token. The authentication process includes
     * validation of email and password and checks if the user exists in the system.
     *
     * @param authenticationRequest the {@link AuthenticationRequest} object containing the user's email and password
     * @return a {@link ResponseEntity} containing the {@link AuthenticationResponse} with user information, access
     *         token, and refresh token upon successful authentication
     */
     @PostMapping("/authenticate")
     public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
     log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
     AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
     log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
     return ResponseEntity.ok(authenticationResponse);
     }

    /**
     * Authenticates a user by verifying the provided credentials.
     * Sets access and refresh tokens as cookies in the HTTP response.
     *
     * @param authenticationRequest The authentication request containing user credentials (email and password).
     * @param response              The HTTP response, containing access and refresh tokens as cookies.
     * @return An authentication response containing the authenticated user's information (ID, first name, last name,
     *         and email).
     */
    /**
     @PostMapping("/authenticate")
     public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
     log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
     AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest, response);
     log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
     return ResponseEntity.ok(authenticationResponse);
     }
     */

    /**
     * Handles a token refresh request by processing the request and updating the {@link HttpServletResponse} with a new
     * access token and other user information. The token refresh process includes validation of the provided refresh
     * token and generating a new access token.
     *
     * @param request  the {@link HttpServletRequest} object containing the client request with the refresh token in
     *                 the "Authorization" header
     * @param response the {@link HttpServletResponse} object that will be updated with the new access token and
     *                 other user information
     * @throws IOException if an input or output error is detected when the servlet handles the token refresh request
     */
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("[X] Received token refresh request");
        authenticationService.handleTokenRefreshRequest(request, response);
        log.info("[X] Token refresh request successful");
    }

    /**
     * Refreshes a user's access token using the provided refresh token.
     * Updates the access token cookie in the HTTP response.
     *
     * @param request  The HTTP request, containing the refresh token in the Authorization header.
     * @param response The HTTP response, containing the updated access token as a cookie.
     * @throws IOException If an error occurs while writing the authentication response to the output stream.
     */
    /**
     @PostMapping("/refresh-token")
     public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
     log.debug("[X] Received token refresh request");
     authenticationService.handleTokenRefreshRequest(request, response);
     log.info("[X] Token refresh request successful");
     }
     */
}