package edu.ntnu.idatt2106_09.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * A service to handle JWT operations such as generating, parsing and validating tokens.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    /**
     * Extracts the username from a provided JWT token.
     *
     * @param token The JWT token to extract the username from.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the provided JWT token using the provided claimsResolver.
     *
     * @param token The JWT token to extract a claim from.
     * @param claimsResolver A function to extract a specific claim from the token.
     * @return The value of the extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token The JWT token to extract the claims from.
     * @return The Claims object containing all the claims in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates an access token for the provided UserDetails.
     *
     * @param userDetails The UserDetails for which to generate the token from.
     * @return The generated access token.
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates an access token with extra claims for the provided UserDetails. This could for example be authorities,
     * or any information you want to store within the token.
     *
     * @param extraClaims A map of extra claims to include in the token.
     * @param userDetails The UserDetails for which to generate the token from.
     * @return The generated access token.
     */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    /**
     * Generates a refresh token for the provided UserDetails.
     *
     * @param userDetails The UserDetails for which to generate the token from.
     * @return The generated refresh token.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    /**
     * Builds a JWT token with the given claims, user details and token expiration.
     *
     * @param extraClaims A map of extra claims to include in the token.
     * @param userDetails The UserDetails for which to generate the token for a user from.
     * @param tokenExpiration The token expiration time in milliseconds.
     * @return The generated JWT token.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long tokenExpiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the provided JWT token for a given user by checking that the username passed within the token
     * matches the username in the UserDetails.
     *
     * @param token The JWT token to check the validity of.
     * @param userDetails The UserDetails for which to validate the token
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the provided JWT token is expired.
     *
     * @param token The JWT token to check the expiration on.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractTokenExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token The JWT token to find the expiration from.
     * @return The expiration date of the token.
     */
    private Date extractTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Retrieves the sign in key to be used for token generation and validation.
     *
     * @return The signing key generated.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets the constant representing for how long a token is valid.
     *
     * @return The access token expiration time in milliseconds.
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Gets the constant representing for how long a token is valid.
     *
     * @return The refresh token expiration time in milliseconds.
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

}
