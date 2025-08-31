package com.sobow.secureweb.repositories;

import com.sobow.secureweb.domain.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // Find profile by user ID
    Optional<UserProfile> findByUserId(Long userId);
}
