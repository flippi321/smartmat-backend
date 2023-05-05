package edu.ntnu.idatt2106_09.backend.config;

import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * LogoutService is responsible for handling user logout operations. It implements LogoutHandler from the Spring
 * Security framework.
 *
 * This class provides the necessary functionality to invalidate the user's access token and clear the
 * SecurityContextHolder during logout.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Handles user logout by invalidating the given access token and clearing the SecurityContextHolder.
     *
     * @param request The HttpServletRequest object containing the client request information.
     * @param response The HttpServletResponse object to send a response back to the client.
     * @param authentication The Authentication object representing the authenticated user.
     * @throws BadRequestException if the token format in the request header is invalid.
     * @throws NotFoundException if the token is not found in the TokenRepository.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format in the request header.");
        }

        final String token = authorizationHeader.substring(7);
        var storedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token not found in the repository."));
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
