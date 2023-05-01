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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // For security reasons we need at the minimum a sign-in key with a size of minimum 256 bits.
    private static final String SECRET_KEY = "48404D6351665468576D5A7134743777217A25432A462D4A614E645267556B58";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                // In the context of jwt, a sign in key is a secret that is used to digitally sign the jwt. It's used to
                // create the signature part of the jwt, which is used to verify that the sender of the jwt is who he/she
                // claims to be and that the message wasn't changed along the way.
                // The sign in key is used in conjunction with the sign in algorithm, specified in the jwt header to create
                // the signature. The specific sign-in algorithm and key-size will depend on the security requirement of
                // your application and the level of trust you have in the sign-in party.
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     *
     * @param extraClaims If you want to pass extra claims, for example: authorities, any information you want to store
     *                    within the token.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // Generates and returns the token.
    }

    /**
     * Checks that the token passed in belongs to the UserDetails passed in by checking that the username passed within
     * the token matches the username in the UserDetails.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractTokenExpiration(token).before(new Date());
    }

    private Date extractTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Base64 decoding the secret key.
        return Keys.hmacShaKeyFor(keyBytes); // Using a hashing-algorithm to hash the secret key.
    }
}
