package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.security.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService{
    
    // In order to generate JWT we are going to need secret
    @Value("${jwt.secret}")
    private String secretKey;
    
    private static final long TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour in miliseconds
    
    @Override
    public String generateToken(CustomUserDetails customUserDetails) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_VALIDITY);
        
        User user = customUserDetails.getUser();
        
        // now we can build our JWT token
        return Jwts.builder()
            .subject(user.getUsername())// Sets the sub claim to username. (must have)
            .issuedAt(now)              // Sets the iat claim when the token was issued.
            .expiration(validity)       // Sets the exp claim when the token should expire. (must have)
            .signWith(getSigningKey())  // Signs the JWT using a secret key in your case, HMAC-SHA. (must have)
                   // The server validates signature every time the token is received. Using secret key.
            .compact();                 // Final step: builds the token into a string.
    }
    
    private Key getSigningKey() {
        byte[] bytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }
}
