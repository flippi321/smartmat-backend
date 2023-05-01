package edu.ntnu.idatt2106_09.backend.config;

import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Holds all the application configurations, such as beans etc.
 * At start-up Spring will pick up this class and try to implement and inject all the beans that we'll declare within
 * this application configuration.
 *
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository repository;

    /**
     * Fetches and return the username from the database using the UserRepository.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The requested user was not found"));
    }
}
