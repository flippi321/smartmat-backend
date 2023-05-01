package edu.ntnu.idatt2106_09.backend.authentication;

import edu.ntnu.idatt2106_09.backend.configuration.JwtService;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.user.Role;
import edu.ntnu.idatt2106_09.backend.user.User;
import org.springframework.security.authentication.AuthenticationManager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        var token = jwtService.generateToken(user); // Creating a token using the 'user' object.

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // An exception will be thrown if the username (email) or password is wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // At this point the user is authenticated.

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

}
