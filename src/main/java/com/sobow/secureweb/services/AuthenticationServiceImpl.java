package com.sobow.secureweb.services;

import com.sobow.secureweb.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    
    private final UserDetailsManager userDetailsManager;
    
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserDetailsManager userDetailsManager) {
        this.authenticationManager = authenticationManager;
        this.userDetailsManager = userDetailsManager;
    }
    
    @Override
    public CustomUserDetails authenticate(String username, String password) {
        // this will throw exception in case of invalid credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
        
        return (CustomUserDetails) userDetailsManager.loadUserByUsername(username);
    }
}
