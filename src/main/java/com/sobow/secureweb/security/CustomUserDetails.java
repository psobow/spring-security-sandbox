package com.sobow.secureweb.security;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.User;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    
    private final User user;
    
    public CustomUserDetails(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                   .map(Authority::getAuthority)
                   .map(SimpleGrantedAuthority::new)
                   .collect(Collectors.toSet());
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
    
    public User getUser() {
        return user;
    }
    
    /*
    We need hashCode and equals in order to determine if two custom users details are the same or different. Do they
     reference the same user or a different user?
    And that's used in features such as session management.
    if we don't implement this correctly, then the security configuration which restricts a user to only one login
    won't work
    because spring will have no way of knowing if it's the same user or a different user
     */
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(user, that.user);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }
}