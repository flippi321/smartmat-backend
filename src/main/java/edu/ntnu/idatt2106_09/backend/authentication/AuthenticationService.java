package edu.ntnu.idatt2106_09.backend.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106_09.backend.config.JwtService;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.token.Token;
import edu.ntnu.idatt2106_09.backend.token.TokenRepository;
import edu.ntnu.idatt2106_09.backend.token.TokenType;
import edu.ntnu.idatt2106_09.backend.model.user.Role;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    /**
     * This will allow us to create a user, save it to the database and return the generated token out of it.
     */
    public AuthenticationResponse register(RegistrationRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = userRepository.save(user);
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
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // TODO throw exception if the username (email) or password is wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // At this point the user is authenticated.

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
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
