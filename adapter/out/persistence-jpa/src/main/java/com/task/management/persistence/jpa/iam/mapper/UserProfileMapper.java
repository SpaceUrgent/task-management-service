package com.task.management.persistence.jpa.iam.mapper;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.util.Objects;


public class UserProfileMapper {
    public static final UserProfileMapper INSTANCE = new UserProfileMapper();

    private UserProfileMapper() {
    }

    public UserInfo toModel(UserEntity userEntity) {
        Objects.requireNonNull(userEntity, "User entity is required");
        return UserInfo.builder()
                .id(new UserId(userEntity.getId()))
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(new Email(userEntity.getEmail()))
                .build();
    }
}
