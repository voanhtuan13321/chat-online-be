package com.chat.chat_online_be.security;

import com.chat.chat_online_be.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Provides methods for generating and verifying JWT tokens.
 * <p>
 * The JWT (JSON Web Token) is a compact and URL-safe means of representing
 * claims to be transferred between two parties. The token is digitally signed
 * and contains a payload that can be verified and trusted.
 * <p>
 * This class generates JWT tokens for the given user details and verifies
 * the tokens by checking the signature and the expiration time.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtTokenProvider {
    @Value("${app.jwtSecret}")
    String _jwtSecret;

    /**
     * Generates a JWT token for the given user.
     * The token includes a standard JWT header and claims generated from the user entity.
     *
     * @param userEntity The user for whom the token is generated.
     * @return The generated JWT token as a String.
     */
    public String generateToken(UserEntity userEntity) {
        // Create a standard JWT header with type "JWT"
        Map<String, Object> headers = Map.of("typ", "JWT");

        // Generate claims (payload) for the user
        Claims claims = genarateClaims(userEntity);

        // Build and sign the JWT token using HS256 algorithm
        return Jwts.builder()
                .setHeaderParams(headers) // Set custom header parameters
                .setClaims(claims) // Set the token payload (claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key and algorithm
                .compact(); // Build and serialize the JWT token to a compact, URL-safe string
    }

    /**
     * Generate a JWT Claims object for the given user.
     * @param userEntity the user for whom to generate claims
     * @return Claims containing both standard and custom fields
     */
    private Claims genarateClaims(UserEntity userEntity) {
        long now = System.currentTimeMillis();
        Claims claims = Jwts.claims();

        claims.setSubject(userEntity.getEmail()); // Standard claim: user's email as the subject (main identifier)
        claims.setIssuedAt(new Date(now)); // Standard claim: token issued at current time
        claims.setExpiration(new Date(now + 1000 * 60 * 5)); // Standard claim: token expiration time (5 minutes from now)
        claims.setId(UUID.randomUUID().toString()); // Standard claim: unique JWT ID
        claims.setIssuer("chat_online_be"); // Standard claim: issuer of the token (your backend system)
        claims.setAudience("chat_online_fe"); // Standard claim: intended audience (e.g., frontend client)
        claims.setNotBefore(new Date(now)); // Standard claim: token is not valid before this time
        claims.put("user_id", userEntity.getId()); // Custom claim: user's unique ID
        claims.put("user_name", userEntity.getUsername()); // Custom claim: user's username
        claims.put("authorities", userEntity.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()); // Custom claim: user's authorities/roles

        return claims;
    }

    /**
     * Retrieves the signing key used for generating and validating JWT tokens.
     * This method decodes the base64-encoded SECRET_KEY from the environment properties
     * and constructs an HMAC SHA key.
     *
     * @return The signing key.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(_jwtSecret));
    }

    /**
     * Given a JWT token, returns the username contained within it.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String getSubjectFromJWT(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates the given JWT token against the provided user details.
     * This method extracts the username from the token and compares it
     * with the username from the user details. It also checks if the token
     * is expired.
     *
     * @param token      The JWT token to be validated.
     * @param userEntity The user details for which the token needs to be validated.
     * @return True if the token is valid for the given user details, false otherwise.
     */
    public boolean isTokenValid(String token, UserEntity userEntity) {
        String email = getSubjectFromJWT(token);
        return (email.equals(userEntity.getEmail()) && !isTokenExpired(token));
    }

    /**
     * Checks if the given JWT token is expired.
     * This method extracts the expiration date from the token and compares it
     * with the current time. If the token is expired, it returns true, otherwise
     * false.
     *
     * @param token The JWT token to be checked for expiration.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the given JWT token.
     * This method calls the overloaded {@link #extractClaim(String, Function)} method with the
     * {@link Claims#getExpiration()} function to extract the expiration date from the token.
     *
     * @param token The JWT token from which the expiration date needs to be extracted.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from the given JWT token using the given resolver.
     * This method takes a JWT token and a function to extract a specific claim
     * from the Claims object. It then calls the resolver with the Claims object
     * and returns the result.
     *
     * @param token          The JWT token from which the claim needs to be extracted.
     * @param claimsResolver The function to extract the claim from the Claims object.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the given JWT token.
     * This method uses the parser builder to set the signing key and then
     * parses the token using the Jwts library. The resulting claims object
     * is then returned.
     *
     * @param token The JWT token to be parsed.
     * @return The parsed claims object.
     */
    private Claims extractAllClaims(String token) {
        // Use the parser builder to set the signing key and then parse the token
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}