package edu.ntnu.idatt2106_09.backend.config;

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
 *
 * At start-up Spring will pick up this class and try to implement and inject all the beans that we'll declare within
 * this application configuration.
 */
@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

    private final UserRepository userRepository;

    /**
     * Provides a UserDetailsService implementation that fetches a user from the database using the UserRepository.
     *
     * @return an instance of UserDetailsService.
     * @throws UsernameNotFoundException if the requested user is not found in the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The requested user was not found"));
    }

    /**
     * Creates a DaoAuthenticationProvider bean, which is responsible for fetching user details and encoding passwords.
     * This bean is used for authentication purposes within the application.
     *
     * @return an instance of AuthenticationProvider (DaoAuthenticationProvider)
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
     * Configures and returns an AuthenticationManager instance that manages the authentication process.
     *
     * @param configuration an instance of AuthenticationConfiguration.
     * @return an instance of AuthenticationManager.
     * @throws Exception if an error occurs during the configuration of the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Provides a PasswordEncoder implementation (BCryptPasswordEncoder) to be used for encoding and decoding passwords.
     *
     * @return an instance of PasswordEncoder (BCryptPasswordEncoder).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
