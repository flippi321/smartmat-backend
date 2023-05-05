package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.authentication.*;
import edu.ntnu.idatt2106_09.backend.service.security.JwtService;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.model.user.Role;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import edu.ntnu.idatt2106_09.backend.model.token.Token;
import edu.ntnu.idatt2106_09.backend.repository.TokenRepository;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.service.security.AuthenticationService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
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

    private User testUser;
    private RegistrationRequest registrationRequest;
    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        registrationRequest = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password");
        authenticationRequest = new AuthenticationRequest("john.doe@example.com", "password");
    }

    @Test
    void register_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.register(registrationRequest);

        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getFirstname(), response.getFirstname());
        assertEquals(testUser.getLastname(), response.getLastname());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void register_emailAlreadyExists() {
        RegistrationRequest registrationRequest = new RegistrationRequest("John", "Doe", "user@example.com", "password");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> authenticationService.register(registrationRequest));

        verify(userRepository).findByEmail("user@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(tokenRepository, jwtService, passwordEncoder, authenticationManager);
    }

    @Test
    void authenticate_success() {
        String email = "user@example.com";
        String password = "password";

        User user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail(email);
        user.setPassword("encoded_password");

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getFirstname(), response.getFirstname());
        assertEquals(user.getLastname(), response.getLastname());
        assertEquals(user.getEmail(), response.getEmail());

        verify(userRepository).findByEmail(email);
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(tokenRepository).findAllValidTokenByUser(user.getId());
        verify(tokenRepository).save(tokenCaptor.capture());

        Token savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertEquals("accessToken", savedToken.getToken());
        assertFalse(savedToken.isRevoked());
        assertFalse(savedToken.isExpired());
    }

    @Test
    void handleTokenRefreshRequest_success() throws IOException {
        // Given
        User user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.extractUsername(refreshToken)).thenReturn(user.getEmail());
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // When
        authenticationService.handleTokenRefreshRequest(request, response);

        // Then
        verify(userRepository).findByEmail(user.getEmail());
        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService).generateAccessToken(user);
        verify(response).setContentType("application/json");
    }

}
