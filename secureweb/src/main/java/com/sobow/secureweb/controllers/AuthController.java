package com.sobow.secureweb.controllers;

import com.sobow.secureweb.domain.DTO.JwtAuthRequestDto;
import com.sobow.secureweb.domain.DTO.JwtAuthResponseDto;
import com.sobow.secureweb.security.CustomUserDetails;
import com.sobow.secureweb.services.AuthenticationService;
import com.sobow.secureweb.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    
    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }
    
    @PostMapping(path = "/login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody JwtAuthRequestDto authRequestDto) {
        CustomUserDetails customUserDetails = authenticationService.authenticate(
            authRequestDto.username(),
            authRequestDto.password()
        );
        String tokenValue = jwtService.generateToken(customUserDetails);
        JwtAuthResponseDto authResponseDto = new JwtAuthResponseDto(tokenValue);
        return ResponseEntity.ok(authResponseDto);
    }
}
