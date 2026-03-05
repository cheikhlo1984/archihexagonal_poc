package com.carrefour.delivery.domain.port.out;

import com.carrefour.delivery.domain.model.User;

import java.util.Optional;

/** Driven port: user persistence contract. */
public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User save(User user);
}
