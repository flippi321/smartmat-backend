package edu.ntnu.idatt2106_09.backend.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationRequest;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationResponse;
import edu.ntnu.idatt2106_09.backend.authentication.RegistrationRequest;
import edu.ntnu.idatt2106_09.backend.service.security.JwtService;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.InternalServerErrorException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.repository.TokenRepository;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.model.token.Token;
import edu.ntnu.idatt2106_09.backend.model.token.TokenType;
import edu.ntnu.idatt2106_09.backend.model.user.Role;
import edu.ntnu.idatt2106_09.backend.model.user.User;
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

    /**
     * Saves the user's access token to the token repository.
     *
     * @param user        The user whose access token is to be saved.
     * @param accessToken The user's access token.
     */
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

    /**
     * Revokes all valid tokens for the specified user by marking them as expired and revoked.
     *
     * @param user The user whose tokens should be revoked.
     */
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

    /**
     * Registers a new user by validating the provided registration details and saving the user in the system. The
     * method generates an access token and a refresh token for successful registration. If the registration is successful,
     * a new {@link AuthenticationResponse} object is returned to the client, containing the user's information, access
     * token, and refresh token.
     *
     * @param request the {@link RegistrationRequest} object containing the user's registration details, such as
     *                first name, last name, email, and password
     * @return an {@link AuthenticationResponse} object containing the user's information, access token, and refresh token
     * @throws BadRequestException if the provided email is already registered, or any of the required fields are empty
     *      *                      or null.
     * @throws InternalServerErrorException if an unexpected error occurs during the registration process
     */
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

    /**
     * Validates the registration request by checking if the provided email is already registered and ensuring that
     * none of the required fields are empty or null.
     *
     * @param request The registration request containing the user's information.
     * @throws BadRequestException if the provided email is already registered, or any of the required fields (firstname,
     *                             lastname, email and password) are empty or null.
     */
    private void validateRegistrationRequest(RegistrationRequest request) throws IOException {
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

    /**
     * Authenticates a user by validating their credentials and generating an access token and a refresh token for
     * successful authentication. The method uses the {@link AuthenticationManager} to validate the provided email and
     * password and checks if the user exists in the system. If the authentication is successful, a new access token and
     * refresh token are generated, and an {@link AuthenticationResponse} object is returned to the client.
     *
     * @param request the {@link AuthenticationRequest} object containing the user's email and password for authentication
     * @return an {@link AuthenticationResponse} object containing the user's information, access token, and refresh token
     * @throws BadCredentialsException if the provided email and password combination is invalid or the user is not found
     * @throws InternalServerErrorException if an unexpected error occurs during the authentication process
     */
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
     * Validates the authentication request by ensuring that none of the required fields (email and password) are
     * empty or null.
     *
     * @param request The authentication request containing the user's email and password.
     * @throws BadCredentialsException if any of the required fields (email and password) are empty or null.
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

    /**
     * Handles a token refresh request by validating the provided refresh token, generating a new access token, and
     * returning an updated authentication response to the client. This method ensures the security of the
     * authentication process by validating the tokens and checking for the existence of the user in the system.
     *
     * @param request  the {@link HttpServletRequest} object that contains the client request, including the refresh
     *                 token in the "Authorization" header.
     * @param response the {@link HttpServletResponse} object that will contain the updated authentication response
     *                 with a new access token and other user information.
     * @throws BadRequestException if the token format in the request header is invalid.
     * @throws NotFoundException if the user is not found for the provided refresh token.
     * @throws BadCredentialsException if the provided refresh token is invalid.
     * @throws IOException if an error occurs while writing the authentication response to the output stream.
     * @throws InternalServerErrorException if an unexpected error occurs during token refresh.
     */
    public void handleTokenRefreshRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format in the request header.");
        }

        final String refreshToken = authorizationHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new NotFoundException("User not found for the provided refresh token.");
        }

        var user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));

        try {
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new BadCredentialsException("Invalid refresh token.");
            }

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

            log.info("[X] Token refresh successful for email: {}", userEmail);
        } catch (BadCredentialsException e) {
            log.warn("[X] Failed to refresh token for user with email: {} - Invalid refresh token", userEmail);
            throw e;
        } catch (Exception e) {
            log.error("[X] Unexpected error during token refresh for user with email: {}", userEmail, e);
            throw new InternalServerErrorException("An unexpected error occurred during token refresh.");
        }
    }
}
