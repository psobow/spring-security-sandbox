package com.sobow.secureweb;


import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.domain.UserProfile;
import com.sobow.secureweb.security.CustomUserDetails;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public final class TestDataUtil {
    
    private TestDataUtil(){
        // Utility class = hidden constructor
    }
    
    public static CustomUserDetails createTestUser() {
        User user = new User();
        
        Authority authority = new Authority();
        authority.setAuthority("USER");
        authority.setUser(user);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        
        UserProfile userProfile = new UserProfile();
        userProfile.setSalary(new BigDecimal(999999));
        userProfile.setUser(user);
        
        user.setUsername("user");
        user.setPassword("password");
        user.setAuthorities(authorities);
        user.setProfile(userProfile);
        
        return new CustomUserDetails(user);
    }
}