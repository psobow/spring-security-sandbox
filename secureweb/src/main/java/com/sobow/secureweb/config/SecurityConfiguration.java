package com.sobow.secureweb.config;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.repositories.AuthorityRepository;
import com.sobow.secureweb.repositories.UserRepository;
import com.sobow.secureweb.security.CustomUserDetails;
import com.sobow.secureweb.security.CustomUserDetailsManager;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(authorize -> authorize
            // Order matters - rules are evaluated in order, with the first match determining access
            // More specific patterns should come before more general ones
                .requestMatchers("/login", "/error").permitAll()
                .requestMatchers("/api/public-data").permitAll()
                .requestMatchers("/api/private**").authenticated()
                .requestMatchers("/admin/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(SessionFixationConfigurer::changeSessionId)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/login?logout")
            )
            .rememberMe(remember -> remember
                .key("secret-key")
                .tokenValiditySeconds(86400)
            )
        ;
        
        return httpSecurity.build();
    }
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
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
            
            Authority userDelete = new Authority();
            userDelete.setAuthority("USER_DELETE");
            userDelete.setUser(customUser);
            
            Set<Authority> authorities = new HashSet<>();
            authorities.add(adminRole);
            authorities.add(userDelete);
            
            UserProfile userProfile = new UserProfile();
            userProfile.setSalary(new BigDecimal(123456));
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
