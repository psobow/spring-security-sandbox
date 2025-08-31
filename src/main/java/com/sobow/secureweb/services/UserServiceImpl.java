package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.repositories.UserRepository;
import com.sobow.secureweb.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public User createUser(String username, String password, BigDecimal salary, String... roles) {
        User customUser = new User();
        
        UserProfile userProfile = new UserProfile();
        userProfile.setSalary(salary);
        userProfile.setUser(customUser);
        
        Set<Authority> authorities = Arrays.stream(roles)
                                           .map(role -> {
                                               Authority authority = new Authority();
                                               authority.setAuthority(role);
                                               authority.setUser(customUser);
                                               return authority;
                                           }).collect(Collectors.toSet());
        
        customUser.setUsername(username);
        customUser.setPassword(passwordEncoder.encode(password));
        customUser.setProfile(userProfile);
        customUser.setAuthorities(authorities);
        
        UserDetails customUserDetails = new CustomUserDetails(customUser);
        
        userDetailsManager.createUser(customUserDetails);
        return userRepository.findByUsername(customUserDetails.getUsername())
                      .orElseThrow(() -> new IllegalStateException("User was not created in the database"));
    }
    
    @Override
    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }
    
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        userDetailsManager.changePassword(oldPassword, newPassword);
    }
    
    @Override
    public List<User> listUsers() {
        return userRepository.findAllWithAuthorities();
    }
}
