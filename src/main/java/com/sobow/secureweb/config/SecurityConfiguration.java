package com.sobow.secureweb.config;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.repositories.AuthorityRepository;
import com.sobow.secureweb.repositories.UserRepository;
import com.sobow.secureweb.security.CustomUserDetails;
import com.sobow.secureweb.security.CustomUserDetailsManager;
import com.sobow.secureweb.security.JwtAuthenticationFilter;
import com.sobow.secureweb.services.JwtService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsManager userDetailsManager,
                                                           JwtService jwtService) {
        return new JwtAuthenticationFilter(userDetailsManager, jwtService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        
        httpSecurity
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public-data").permitAll()
                .requestMatchers("/api/private**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//            .cors(cors -> cors
//                .configurationSource(corsConfigurationSource())
//            )
        ;
        
        return httpSecurity.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("*");
//        configuration.addAllowedMethod("GET");
//        configuration.addAllowedHeader("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", configuration);
//
//        return source;
//    }
    
    @Bean
    public UserDetailsManager userDetailsManager(
        UserRepository userRepository,
        AuthorityRepository authorityRepository
    ) {
        CustomUserDetailsManager customUserDetailsManager = new CustomUserDetailsManager(
            userRepository,
            authorityRepository,
            passwordEncoder()
        );
        
        if (!customUserDetailsManager.userExists("user")) {
            User customUser = new User();
            
            Authority adminRole = new Authority();
            adminRole.setAuthority("ROLE_ADMIN");
            adminRole.setUser(customUser);
            
            Set<Authority> authorities = new HashSet<>();
            authorities.add(adminRole);
            
            UserProfile userProfile = new UserProfile();
            userProfile.setSalary(new BigDecimal(1234569));
            userProfile.setUser(customUser);
            
            customUser.setUsername("user");
            customUser.setPassword(passwordEncoder().encode("password"));
            customUser.setAuthorities(authorities);
            customUser.setProfile(userProfile);
            UserDetails customUserDetails = new CustomUserDetails(customUser);
            
            customUserDetailsManager.createUser(customUserDetails);
        }
        
        return customUserDetailsManager;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
