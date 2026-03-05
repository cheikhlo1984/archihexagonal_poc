package com.carrefour.delivery.infrastructure.persistence.adapter;

import com.carrefour.delivery.domain.model.User;
import com.carrefour.delivery.domain.port.out.UserRepositoryPort;
import com.carrefour.delivery.infrastructure.persistence.entity.UserEntity;
import com.carrefour.delivery.infrastructure.persistence.mapper.UserEntityMapper;
import com.carrefour.delivery.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Driven adapter: bridges the domain UserRepositoryPort to JPA. */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
