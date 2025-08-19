package com.sobow.secureweb.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/public-data").permitAll()
                .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.OPTIONS, "^/api/public.*$")).permitAll()
                .requestMatchers("/api/private**").authenticated()
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
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        /*
        UserDetails userOne = User.builder()
                               .username("user")
                               .password(passwordEncoder().encode("password"))
                               .roles("USER")
                               .build();
        
        UserDetails userTwo = User.builder()
                                  .username("admin")
                                  .password(passwordEncoder().encode("password"))
                                  .roles("ADMIN")
                                  .build();
        
        return new InMemoryUserDetailsManager(userOne, userTwo);
         */
        
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        
        if (!jdbcUserDetailsManager.userExists("user")) {
            UserDetails admin = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();
            jdbcUserDetailsManager.createUser(admin);
        }
        return jdbcUserDetailsManager;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
