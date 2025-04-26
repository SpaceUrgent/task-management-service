package com.task.management.persistence.jpa.common.mapper;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.persistence.jpa.entity.UserEntity;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class UserInfoMapper {
    public static final UserInfoMapper INSTANCE = new UserInfoMapper();

    private UserInfoMapper() {}

    public UserInfo toModel(UserEntity entity) {
        parameterRequired(entity, "User entity");
        return UserInfo.builder()
                .id(new UserId(entity.getId()))
                .email(new Email(entity.getEmail()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }
}
