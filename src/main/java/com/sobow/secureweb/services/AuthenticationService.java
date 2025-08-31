package com.sobow.secureweb.services;

import com.sobow.secureweb.security.CustomUserDetails;

public interface AuthenticationService {
    CustomUserDetails authenticate(String username, String password);
}
