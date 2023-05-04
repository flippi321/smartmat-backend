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

/**
 * A filter that processes incoming requests for JWT tokens stored in cookies.
 * This filter is executed once per request and checks for the presence of an "access_token" cookie.
 * If the cookie is found, the filter adds the token to the "Authorization" header with the "Bearer" prefix.
 */
public class JwtCookieFilter extends OncePerRequestFilter {

    /**
     * Searches for the "access_token" cookie in the request and, if found, adds its value to the "Authorization" header.
     *
     * @param request the HttpServletRequest being processed.
     * @param response the HttpServletResponse being processed.
     * @param filterChain the filter chain that this filter is part of
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an input or output error occurs during request processing
     */

    /**
     * Searches for the "access_token" cookie in the request and, if found, adds its value to the "Authorization" header.
     * This is done to allow the JwtAuthenticationFilter to validate the token. The JwtAuthenticationFilter only checks
     * the "Authorization" header for a valid token.
     * @param request the HttpServletRequest being processed.
     * @param response the HttpServletResponse being processed.
     * @param filterChain the filter chain that this filter is part of.
     * @throws ServletException if an error occurs during request processing.
     * @throws IOException if an input or output error occurs during request processing.
     */
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

    /**
     * Returns a new HttpServletRequest that overrides the "Authorization" header value. The new header value is the one
     * provided as a parameter. All other header values are the same as the ones in the original request. This method
     * is used to override the "Authorization" header with the value of the access token cookie. This is done to
     * allow the JwtAuthenticationFilter to validate the token. The JwtAuthenticationFilter only checks the
     * "Authorization" header for a valid token.
     * @param request the original HttpServletRequest object to override the "Authorization" header value for.
     * @param headerValue the new value for the "Authorization" header.
     * @return a new HttpServletRequest object with the "Authorization" header overridden.
     */
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
