package com.task.management.persistence.jpa.iam.mapper;

import com.task.management.domain.common.Email;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.util.Objects;


public class UserProfileMapper {
    public static final UserProfileMapper INSTANCE = new UserProfileMapper();

    private UserProfileMapper() {
    }

    public UserProfile toModel(UserEntity userEntity) {
        Objects.requireNonNull(userEntity, "User entity is required");
        return UserProfile.builder()
                .id(new UserId(userEntity.getId()))
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(new Email(userEntity.getEmail()))
                .build();
    }
}
