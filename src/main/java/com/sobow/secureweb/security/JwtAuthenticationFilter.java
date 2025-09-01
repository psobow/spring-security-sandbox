package com.sobow.secureweb.security;

import com.sobow.secureweb.domain.DTO.JwtValidationResult;
import com.sobow.secureweb.services.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final UserDetailsManager userDetailsManager;
    private final JwtService jwtService;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    public JwtAuthenticationFilter(UserDetailsManager userDetailsManager, JwtService jwtService) {
        this.userDetailsManager = userDetailsManager;
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        
        try {
            String token = extractToken(request);
            if (token != null) {
                JwtValidationResult jwtValidationResult = jwtService.validateAccessToken(token);
                if (jwtValidationResult.isValid()) {
                    String username = jwtValidationResult.subject();
                    CustomUserDetails customUserDetails =
                        (CustomUserDetails) userDetailsManager.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException exception) {
            logger.warn("Invalid JWT", exception);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
