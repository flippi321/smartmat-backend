package edu.ntnu.idatt2106_09.backend.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106_09.backend.configuration.JwtService;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.token.Token;
import edu.ntnu.idatt2106_09.backend.token.TokenRepository;
import edu.ntnu.idatt2106_09.backend.token.TokenType;
import edu.ntnu.idatt2106_09.backend.user.Role;
import edu.ntnu.idatt2106_09.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    private void createUserToken(User user, String userToken) {
        var token = Token.builder()
                .user(user)
                .token(userToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * This will allow us to create a user, save it to the database and return the generated token out of it.
     */
    public AuthenticationResponse register(RegistrationRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encoding the password before saving it to the database.
                .role(Role.USER)
                .build();
        var savedUser = userRepository.save(user); // Saving the user to the repository.
        var generatedAccessToken = jwtService.generateAccessToken(user); // Creating an access token.
        var generatedRefreshToken = jwtService.generateRefreshToken(user); // Creating a refresh token.
        createUserToken(savedUser, generatedAccessToken);

        return AuthenticationResponse.builder()
                // Returning an access token after the registration to make it easy for the user, not making it
                // necessary to reconnect to have an access token to use. This makes it easy to for example directly
                // redirect a user when they register.
                .accessToken(generatedAccessToken)
                .refreshToken(generatedRefreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // An exception will be thrown if the username (email) or password is wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // At this point the user is authenticated.

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var generatedAccessToken = jwtService.generateAccessToken(user); // Creating an access token.
        var generatedRefreshToken = jwtService.generateRefreshToken(user); // Creating a refresh token.
        revokeAllTokensForUser(user);
        createUserToken(user, generatedAccessToken);

        return AuthenticationResponse.builder()
                .accessToken(generatedAccessToken)
                .refreshToken(generatedRefreshToken)
                .build();
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

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authorizationHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            // Should the refresh token also be revoked?
            if (jwtService.isTokenValid(refreshToken, user)) {
                var generatedAccessToken = jwtService.generateAccessToken(user);
                revokeAllTokensForUser(user);
                createUserToken(user, generatedAccessToken);
                var authenticationResponse = AuthenticationResponse.builder()
                        .accessToken(generatedAccessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
            }
        }
    }
}
