package com.sobow.secureweb;


import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.security.CustomUserDetails;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TestDataUtil {
    
    private TestDataUtil(){
        // Utility class = hidden constructor
    }
    
    public static CustomUserDetails createTestUser(String username, int salary, String... authorityValues) {
        User user = new User();
        Set<Authority> authorities = new HashSet<>();
        
        Arrays.stream(authorityValues).forEach(authValue -> {
            Authority authority = new Authority();
            authority.setAuthority(authValue);
            
            authority.setUser(user);
            authorities.add(authority);
        });
        
        UserProfile userProfile = new UserProfile();
        userProfile.setSalary(new BigDecimal(salary));
        userProfile.setUser(user);
        
        user.setUsername(username);
        user.setPassword("password");
        user.setAuthorities(authorities);
        user.setProfile(userProfile);
        
        return new CustomUserDetails(user);
    }
}