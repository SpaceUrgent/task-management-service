package com.task.management.persistence.jpa.mapper;

import com.task.management.domain.iam.model.UserCredentials;
import com.task.management.domain.iam.model.UserId;
import com.task.management.persistence.jpa.entity.UserEntity;

import static java.util.Objects.requireNonNull;

public class UserCredentialsMapper {
    UserCredentialsMapper() {
    }

    public UserCredentials toModel(UserEntity entity) {
        requireNonNull(entity, "Entity is required");
        return new UserCredentials(new UserId(entity.getId()), entity.getEmail(), entity.getEncryptedPassword());
    }
}
