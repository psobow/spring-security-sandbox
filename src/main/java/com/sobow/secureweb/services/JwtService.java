package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.DTO.JwtValidationResult;
import com.sobow.secureweb.security.CustomUserDetails;

public interface JwtService {
    String generateAccessToken(CustomUserDetails customUserDetails);
    String generateRefreshToken(CustomUserDetails customUserDetails);
    
    JwtValidationResult validateAccessToken(String token);
    JwtValidationResult validateRefreshToken(String token);
}
