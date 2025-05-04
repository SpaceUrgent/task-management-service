package com.task.management.persistence.jpa.iam.mapper;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.iam.model.objectvalue.UserCredentials;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.persistence.jpa.entity.UserEntity;

import static java.util.Objects.requireNonNull;

public class UserCredentialsMapper {
    public static final UserCredentialsMapper INSTANCE = new UserCredentialsMapper();

    private UserCredentialsMapper() {
    }

    public UserCredentials toModel(UserEntity entity) {
        requireNonNull(entity, "Entity is required");
        return new UserCredentials(new UserId(entity.getId()), new Email(entity.getEmail()), entity.getEncryptedPassword());
    }
}
