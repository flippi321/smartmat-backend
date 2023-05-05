package edu.ntnu.idatt2106_09.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            // Look for the access_token cookie
            Cookie accessTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> "access_token".equals(cookie.getName()))
                    .findFirst().orElse(null);

            if (accessTokenCookie != null) {
                // Set the Authorization header with the Bearer prefix and the access token value
                request = setAuthorizationHeader(request, "Bearer " + accessTokenCookie.getValue());
            }
        }

        filterChain.doFilter(request, response);
    }

    private HttpServletRequest setAuthorizationHeader(HttpServletRequest request, String headerValue) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("Authorization".equalsIgnoreCase(name)) {
                    return headerValue;
                }
                return super.getHeader(name);
            }
        };
    }
}
