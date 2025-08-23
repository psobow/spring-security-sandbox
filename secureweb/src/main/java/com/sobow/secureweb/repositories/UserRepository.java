package com.sobow.secureweb.repositories;

import com.sobow.secureweb.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username with eager loading of authorities
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authorities WHERE u.username = :username")
    Optional<User> findByUsernameWithAuthorities(String username);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authorities")
    List<User> findAllWithAuthorities();
    
    // Basic username lookup
    Optional<User> findByUsername(String username);
    
    // Check username existence
    boolean existsByUsername(String username);
}