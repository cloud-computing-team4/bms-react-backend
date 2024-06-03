package com.backend.bms.repository;

import com.backend.bms.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
}
