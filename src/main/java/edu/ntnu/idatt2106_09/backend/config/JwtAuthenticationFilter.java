package edu.ntnu.idatt2106_09.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Tells Spring that this class should be a Spring Bean.
@Component
// Will create a constructor which takes in all class variables as arguments.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            // We can intercept every request and for example extract data from it and provide new data within the
            // response. This could be that we wanted to add a header to the response.
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            // The chain-of-responsibility pattern. It contains a list of all the filters we want to execute.
            // If you call filterchain.doFilter() it will call the next filter within the chain.
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // When we make a call, we need to pass the token token within the header. Under, we're extracting this header.
        final String authorizationHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Passing the request and response to the next filter.
            return; // Stops the execution of the method.
        }

        // Extracting the token from the auth-header.
        token = authorizationHeader.substring(7); // Starting from pos no. 7 as we're skipping 'Bearer '.
        // Extracting the user email from the token.
        userEmail = jwtService.extractUsername(token);

        // Checks that the user exists and that it's not already authenticated yet. Because if the user is authenticated,
        // we don't need to perform all the checks in the if-sentence again.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Passing the hand to the next filter to be executed.
        filterChain.doFilter(request, response);
    }
}
