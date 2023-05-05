package edu.ntnu.idatt2106_09.backend.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        log.debug("[X] Received registration request for email: {}", registrationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.register(registrationRequest);
        log.info("[X] Registration request successful for email: {}", registrationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
        log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }
    /**
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        log.debug("[X] Received authentication request for email: {}", authenticationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest, response);
        log.info("[X] Authentication request successful for email: {}", authenticationRequest.getEmail());
        return ResponseEntity.ok(authenticationResponse);
    }*/

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.handleTokenRefreshRequest(request, response);
    }
}