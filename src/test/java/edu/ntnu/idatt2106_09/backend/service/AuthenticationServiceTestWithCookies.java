/**package edu.ntnu.idatt2106_09.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationRequest;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationResponse;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationService;
import edu.ntnu.idatt2106_09.backend.authentication.RegistrationRequest;
import edu.ntnu.idatt2106_09.backend.config.JwtService;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.token.Token;
import edu.ntnu.idatt2106_09.backend.token.TokenRepository;
import edu.ntnu.idatt2106_09.backend.model.user.Role;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTestWithCookies {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123"
        );
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");


        // When
        AuthenticationResponse authResponse = authenticationService.register(registrationRequest, response);

        // Then
        verify(userRepository).findByEmail(registrationRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    void authenticate() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("john.doe@example.com", "password123");
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // When
        AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest, response);

        // Then
        verify(userRepository).findByEmail(authenticationRequest.getEmail());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(tokenRepository).findAllValidTokenByUser(user.getId());
        verify(tokenRepository).save(any(Token.class));
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    void handleTokenRefreshRequest() throws IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Set up the response to return the output stream when getOutputStream() is called
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }
        });

        ObjectMapper objectMapper = new ObjectMapper();

        User user = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        String refreshToken = "refreshToken";
        String authorizationHeader = "Bearer " + refreshToken;

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authorizationHeader);
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");

        // When
        authenticationService.handleTokenRefreshRequest(request, response);

        // Then
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
        verify(jwtService).extractUsername(refreshToken);
        verify(userRepository).findByEmail(user.getEmail());
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService).generateAccessToken(user);
        verify(tokenRepository).findAllValidTokenByUser(user.getId());
        verify(tokenRepository).save(any(Token.class));
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(response).setContentType("application/json");
        verify(response).getOutputStream();
    }
}
 */