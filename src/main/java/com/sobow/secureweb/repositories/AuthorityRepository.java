package com.sobow.secureweb.repositories;

import com.sobow.secureweb.domain.Authority;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // Find all authorities for a user
    Set<Authority> findAllByUserId(Long userId);
    
    // Delete all authorities for a user
    void deleteAllByUserId(Long userId);
}