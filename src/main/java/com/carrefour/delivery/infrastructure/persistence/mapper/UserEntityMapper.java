package com.carrefour.delivery.infrastructure.persistence.mapper;

import com.carrefour.delivery.domain.model.User;
import com.carrefour.delivery.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity(
                domain.getUsername(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getRole()
        );
        entity.setId(domain.getId());
        return entity;
    }
}
