package edu.ntnu.idatt2106_09.backend.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106_09.backend.config.JwtService;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.InternalServerErrorException;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.token.Token;
import edu.ntnu.idatt2106_09.backend.token.TokenRepository;
import edu.ntnu.idatt2106_09.backend.token.TokenType;
import edu.ntnu.idatt2106_09.backend.model.user.Role;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private void saveUserTokenToRepository(User user, String accessToken) {
        var token = Token.builder()
                .user(user)
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllTokensForUser(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse register(RegistrationRequest request) {
        log.debug("[X] Attempting to register a new user with email: {}", request.getEmail());
        try {
            validateRegistrationRequest(request);

            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            var savedUser = userRepository.save(user);
            log.info("[X] User registered successfully: {}", savedUser);

            var generatedAccessToken = jwtService.generateAccessToken(user);
            var generatedRefreshToken = jwtService.generateRefreshToken(user);
            saveUserTokenToRepository(savedUser, generatedAccessToken);

            return AuthenticationResponse.builder()
                    .accessToken(generatedAccessToken)
                    .refreshToken(generatedRefreshToken)
                    .id(savedUser.getId())
                    .firstname(savedUser.getFirstname())
                    .lastname(savedUser.getLastname())
                    .email(savedUser.getEmail())
                    .build();
        } catch (BadRequestException e) {
            log.warn("[X] User registration failed: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[X] Unexpected error during user registration: {} - {}", request.getEmail(), e.getMessage(), e);
            throw new InternalServerErrorException("An unexpected error occurred during registration.");
        }
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new BadRequestException("The provided email is already registered.");
        });

        Map<String, String> fields = Map.of(
                "First name", request.getFirstname(),
                "Last name", request.getLastname(),
                "Email", request.getEmail(),
                "Password", request.getPassword()
        );

        Optional<String> emptyField = fields.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .findFirst();

        emptyField.ifPresent(field -> {
            throw new BadRequestException(field + " cannot be empty or null.");
        });
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.debug("[X] Attempting to authenticate a new user with email: {}", request.getEmail());
        try {
            validateAuthenticationRequest(request);

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));
            log.info("[X] User registered successfully: {}", user);

            var generatedAccessToken = jwtService.generateAccessToken(user);
            var generatedRefreshToken = jwtService.generateRefreshToken(user);
            revokeAllTokensForUser(user);
            saveUserTokenToRepository(user, generatedAccessToken);

            return AuthenticationResponse.builder()
                    .accessToken(generatedAccessToken)
                    .refreshToken(generatedRefreshToken)
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .build();
        } catch (BadCredentialsException e) {
            log.warn("[X] Failed to authenticate user with email: {} - Invalid credentials", request.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("[X] Unexpected error during authentication for user with email: {}", request.getEmail(), e);
            throw new InternalServerErrorException("An unexpected error occurred during authentication.");
        }
    }

    /**
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        // ... (existing authentication code)

        // At this point the user is authenticated.

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var generatedAccessToken = jwtService.generateAccessToken(user);
        var generatedRefreshToken = jwtService.generateRefreshToken(user);
        revokeAllTokensForUser(user);
        saveUserTokenToRepository(user, generatedAccessToken);

        // Create cookies for the access token and refresh token
        Cookie accessTokenCookie = new Cookie("access_token", generatedAccessToken);
        Cookie refreshTokenCookie = new Cookie("refresh_token", generatedRefreshToken);

        // Set HttpOnly, Secure, and SameSite attributes for both cookies
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // Set this to true only for HTTPS connections
        accessTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // Set this to true only for HTTPS connections
        refreshTokenCookie.setPath("/");

        // Add the cookies to the HttpServletResponse
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return AuthenticationResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }
     */


    private void validateAuthenticationRequest(AuthenticationRequest request) {
        Map<String, String> fields = Map.of(
                "Email", request.getEmail(),
                "Password", request.getPassword()
        );

        Optional<String> emptyField = fields.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .findFirst();

        emptyField.ifPresent(field -> {
            throw new BadCredentialsException(field + " cannot be empty or null.");
        });
    }

    public void handleTokenRefreshRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authorizationHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            // TODO Should the refresh token also be revoked?
            // TODO Check for token expiration before using it?
            // TODO Add logic instead of just using 'orElseThrow()'?
            if (jwtService.isTokenValid(refreshToken, user)) {
                var generatedAccessToken = jwtService.generateAccessToken(user);
                revokeAllTokensForUser(user);
                saveUserTokenToRepository(user, generatedAccessToken);
                var authenticationResponse = AuthenticationResponse.builder()
                        .accessToken(generatedAccessToken)
                        .refreshToken(refreshToken)
                        .id(user.getId())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .email(user.getEmail())
                        .build();
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
            }
        }
    }
}
