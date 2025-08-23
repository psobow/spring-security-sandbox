package com.sobow.secureweb.security;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.repositories.AuthorityRepository;
import com.sobow.secureweb.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;


public class CustomUserDetailsManager implements UserDetailsManager {
    
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CustomUserDetailsManager(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithAuthorities(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
    
    @Override
    @Transactional
    public void createUser(UserDetails userDetails) {
        // Cast UserDetails to CustomUserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User userToCreate = customUserDetails.getUser();
        
        // Create a new custom user entity and set values from CustomUserDetails
        User user = new User();
        user.setUsername(userToCreate.getUsername());
        user.setPassword(userToCreate.getPassword());
        user.setEnabled(userToCreate.isEnabled());
        customUserDetails.getAuthorities().forEach(authority -> {
            Authority auth = new Authority();
            auth.setAuthority(authority.getAuthority());
            auth.setUser(user);
            user.getAuthorities().add(auth);
        });
        
        // Extract salary information
        BigDecimal salary = extractSalaryOrThrow(userToCreate);
        
        // Set salary info on a new UserProfile instance
        UserProfile userProfile = new UserProfile();
        userProfile.setSalary(salary);
        
        // Set the profile <-> user relationship
        userProfile.setUser(user);
        user.setProfile(userProfile);
        
        // save the user
        userRepository.save(user);
    }
    
    private BigDecimal extractSalaryOrThrow(User user) {
        return Optional.ofNullable(user.getProfile())
                       .map(UserProfile::getSalary)
                       .orElseThrow(() -> new IllegalArgumentException("A user's salary must be provided"));
    }
    
    @Override
    @Transactional
    public void updateUser(UserDetails userDetails) {
        
        // Cast UserDetails to CustomUserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User userUpdate = customUserDetails.getUser();
        
        // Extract salary
        BigDecimal salaryUpdate = extractSalaryOrThrow(userUpdate);
        
        // Get existing user
        User exisitingUser = userRepository.findByUsername(customUserDetails.getUsername())
                                           .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Update values
        exisitingUser.setPassword(customUserDetails.getPassword());
        exisitingUser.setEnabled(customUserDetails.isEnabled());
        exisitingUser.getProfile().setSalary(salaryUpdate);
        
        // Update authorities
        authorityRepository.deleteAllByUserId(exisitingUser.getId());
        customUserDetails.getAuthorities().forEach(authority -> {
            // set up new authority
            Authority auth = new Authority();
            auth.setAuthority(authority.getAuthority());
            auth.setUser(exisitingUser);
            exisitingUser.getAuthorities().add(auth);
        });
        
        userRepository.save(exisitingUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(String username) {
        userRepository.findByUsername(username)
                      .ifPresent(userRepository::delete);
    }
    
    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        // get currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found");
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Verify the old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        
        // Encode and set the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // save changes
        userRepository.save(user);
        
        // Update the security context to use the updated user details
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
            new CustomUserDetails(user),
            newPassword,
            authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
    
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
