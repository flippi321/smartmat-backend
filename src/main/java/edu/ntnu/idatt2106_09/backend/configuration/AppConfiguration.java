package edu.ntnu.idatt2106_09.backend.configuration;

import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Holds all the application configurations, such as beans etc.
 * At start-up Spring will pick up this class and try to implement and inject all the beans that we'll declare within
 * this application configuration.
 *
 */
@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

    private final UserRepository repository;

    /**
     * Fetches and return the username from the database using the UserRepository.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The requested user was not found"));
    }

    /**
     * This 'AuthenticationProvider' is the DAO which is responsible to fetch the user details also encode passwords etc.
     * For this we have many implementations, one of them being the 'DaoAuthenticationProvider'.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Telling the AuthenticationProvider which UserDetailsService to use in order to fetch information about our users.
        // This is because we might have several implementations of UserDetailsService, for example: one for getting
        // information from the database, one for fetching users from an in-memory database etc.
        authenticationProvider.setUserDetailsService(userDetailsService());
        // When we want to authenticate a user, we need to know which password encoder is used in order to be able to decode
        // the password using the correct algorithm.
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Manages the authentication process.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
