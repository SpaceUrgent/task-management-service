package com.task.management.persistence.jpa.mapper;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class UserMapper {

    public UserEntity toEntity(User user) {
        requireNonNull(user, "User model is required");
        return UserEntity.builder()
                .id(Optional.ofNullable(user.getId()).map(UserId::value).orElse(null))
                .createdAt(Instant.now())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .encryptedPassword(user.getEncryptedPassword())
                .build();
    }

    public User toModel(UserEntity userEntity) {
        requireNonNull(userEntity, "User entity is required");
        return User.builder()
                .id(new UserId(userEntity.getId()))
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .encryptedPassword(userEntity.getEncryptedPassword())
                .build();
    }
}
