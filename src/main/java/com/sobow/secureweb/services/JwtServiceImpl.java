package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.DTO.JwtValidationResult;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
    
    // In order to generate JWT we are going to need secret
    @Value("${jwt.secret.access}")
    private String secretKeyAccess;
    
    @Value("${jwt.secret.refresh}")
    private String secretKeyRefresh;
    
    private static final long TOKEN_VALIDITY_ACCESS = 15 * 60 * 1000; // 15 mins in miliseconds
    private static final long TOKEN_VALIDITY_REFRESH = 7 * 24 * 60 * 60 * 1000; // 7 days in miliseconds
    
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String CLAIM_KEY_TYPE = "type";
    
    @Override
    public String generateAccessToken(CustomUserDetails customUserDetails) {
        return generateToken(customUserDetails, TOKEN_VALIDITY_ACCESS, secretKeyAccess, TOKEN_TYPE_ACCESS);
    }
    
    @Override
    public String generateRefreshToken(CustomUserDetails customUserDetails) {
        return generateToken(customUserDetails, TOKEN_VALIDITY_REFRESH, secretKeyRefresh, TOKEN_TYPE_REFRESH);
    }
    
    private String generateToken(CustomUserDetails customUserDetails, long tokenValidity, String secretKey,
                                 String tokenType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidity);
        User user = customUserDetails.getUser();
        return Jwts.builder()
                   .subject(user.getUsername())// Sets the sub claim to username. (must have)
                   .issuedAt(now)              // Sets the iat claim when the token was issued.
                   .expiration(validity)       // Sets the exp claim when the token should expire. (must have)
                   .claim(CLAIM_KEY_TYPE, tokenType) // custom claim to distinguish access and refresh
                   .signWith(getSigningKey(secretKey))  // Signs the JWT using a secret key in your case, HMAC-SHA. (must have)
                   // The server validates signature every time the token is received. Using secret key.
                   .compact();
    }
    
    @Override
    public JwtValidationResult validateAccessToken(String token) {
        return validateToken(token, secretKeyAccess, TOKEN_TYPE_ACCESS);
    }
    
    @Override
    public JwtValidationResult validateRefreshToken(String token) {
        return validateToken(token, secretKeyRefresh, TOKEN_TYPE_REFRESH);
    }
    
    private JwtValidationResult validateToken(String token, String secretKey, String expectedTokenType) {
        Claims payload = Jwts.parser()
                             .verifyWith(getSigningKey(secretKey)) // Verify signature with your secret key.
                             .build()
                             .parseSignedClaims(token)
                             .getPayload();
        String subject = payload.getSubject();
        String tokenType = payload.get(CLAIM_KEY_TYPE, String.class);
        
        if (expectedTokenType.equals(tokenType) && subject != null) {
            return new JwtValidationResult(true, subject);
        } else {
            return new JwtValidationResult(false, null);
        }
    }
    
    private SecretKey getSigningKey(String secretKey) {
        byte[] bytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }
}
