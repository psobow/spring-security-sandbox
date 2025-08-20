package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.security.CustomUserDetails;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    
    public UserManagementService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void createUser(String username, String password, BigDecimal salary, String... roles) {
        User customUser = new User();
        customUser.setUsername(username);
        customUser.setPassword(passwordEncoder.encode(password));
        
        UserProfile userProfile = new UserProfile();
        userProfile.setSalary(salary);
        userProfile.setUser(customUser);
        customUser.setProfile(userProfile);
        
        Set<Authority> authorities = Arrays.stream(roles)
                                           .map(role -> {
                                               Authority authority = new Authority();
                                               authority.setAuthority(role);
                                               authority.setUser(customUser);
                                               return authority;
                                           }).collect(Collectors.toSet());
        customUser.setAuthorities(authorities);
        
        UserDetails customUserDetails = new CustomUserDetails(customUser);
        
        userDetailsManager.createUser(customUserDetails);
    }
    
    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }
    
    public void changePassword(String oldPassword, String newPassword) {
        userDetailsManager.changePassword(oldPassword, passwordEncoder.encode(newPassword));
    }
}
