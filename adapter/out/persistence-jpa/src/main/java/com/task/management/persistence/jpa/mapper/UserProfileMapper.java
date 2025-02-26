package com.task.management.persistence.jpa.mapper;

import com.task.management.application.iam.model.UserId;
import com.task.management.application.iam.model.UserProfile;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.util.Objects;


public class UserProfileMapper {
    UserProfileMapper() {
    }

    public UserProfile toModel(UserEntity userEntity) {
        Objects.requireNonNull(userEntity, "User entity is required");
        return UserProfile.builder()
                .id(new UserId(userEntity.getId()))
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .build();
    }
}
