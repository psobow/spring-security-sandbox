package com.sobow.secureweb.services;

import com.sobow.secureweb.security.CustomUserDetails;

public interface JwtService {
    String generateToken(CustomUserDetails customUserDetails);
}
