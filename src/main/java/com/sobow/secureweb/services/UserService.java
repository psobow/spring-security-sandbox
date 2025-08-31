package com.sobow.secureweb.services;

import com.sobow.secureweb.domain.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserService {
    
    @PreAuthorize("hasRole('ADMIN')")
    User createUser(String username, String password, BigDecimal salary, String... roles);
    
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(String username);
    
    @PreAuthorize("hasRole('ADMIN')")
    List<User> listUsers();
    
    void changePassword(String oldPassword, String newPassword);
}
