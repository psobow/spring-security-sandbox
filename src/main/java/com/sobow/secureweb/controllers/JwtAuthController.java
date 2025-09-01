package com.sobow.secureweb.controllers;

import com.sobow.secureweb.domain.DTO.JwtAuthRequestDto;
import com.sobow.secureweb.domain.DTO.JwtAuthResponseDto;
import com.sobow.secureweb.domain.DTO.JwtValidationResult;
import com.sobow.secureweb.domain.DTO.RefreshTokenRequest;
import com.sobow.secureweb.security.CustomUserDetails;
import com.sobow.secureweb.services.AuthenticationService;
import com.sobow.secureweb.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth")
public class JwtAuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsManager userDetailsManager;
    
    public JwtAuthController(AuthenticationService authenticationService, JwtService jwtService, UserDetailsManager userDetailsManager) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userDetailsManager = userDetailsManager;
    }
    
    @PostMapping(path = "/login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody JwtAuthRequestDto authRequestDto) {
        CustomUserDetails customUserDetails = authenticationService.authenticate(
            authRequestDto.username(),
            authRequestDto.password()
        );
        String accessToken = jwtService.generateAccessToken(customUserDetails);
        String refreshToken = jwtService.generateRefreshToken(customUserDetails);
        
        JwtAuthResponseDto authResponseDto = new JwtAuthResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok(authResponseDto);
    }
    
    @PostMapping(path = "/refresh")
    public ResponseEntity<JwtAuthResponseDto> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtValidationResult jwtValidationResult = jwtService.validateRefreshToken(refreshTokenRequest.token());
        
        if (!jwtValidationResult.isValid()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = jwtValidationResult.subject();
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsManager.loadUserByUsername(username);
        
        String newAccessToken = jwtService.generateAccessToken(customUserDetails);
        return ResponseEntity.ok(new JwtAuthResponseDto(newAccessToken,null));
    }
    
}
