package com.sobow.secureweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            /*
            // Disable session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Disable security headers
            .headers(headers -> headers.disable())
            // Disable CSRF protection
            .csrf(csrf -> csrf.disable())
            */
            .authorizeHttpRequests( // define authorization config - which HTTP requests require authentication
                                    authorize -> authorize
                                        // Specific path and HTTP method matching
                                        .requestMatchers(HttpMethod.GET, "/api/public-data").permitAll()
                                        // Using regex for OPTIONS endpoint
                                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.OPTIONS, "^/api/public.*$")).permitAll()
                                        // Ant-style pattern matching
                                        .requestMatchers("/api/private**").authenticated()
                                        // Catch-all rule
                                        .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll()) // when omitted it will be disabled
            .logout(logout -> logout.permitAll()) // when omitted it will be disabled
            .httpBasic(basic -> {});
        
        return httpSecurity.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                               .username("user")
                               .password(passwordEncoder().encode("password"))
                               .roles("USER")
                               .build();
        
        return new InMemoryUserDetailsManager(user);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
