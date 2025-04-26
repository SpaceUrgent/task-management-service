package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.projection.MemberView;
import com.task.management.persistence.jpa.entity.MemberEntity;
import com.task.management.persistence.jpa.entity.UserEntity;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class MemberViewMapper {

    public static MemberViewMapper INSTANCE = new MemberViewMapper();

    private MemberViewMapper() {
    }

    public MemberView toModel(UserEntity entity) {
        parameterRequired(entity, "User entity");
        return MemberView.builder()
                .id(new UserId(entity.getId()))
                .email(new Email(entity.getEmail()))
                .fullName("%s %s".formatted(entity.getFirstName(), entity.getLastName()))
                .build();
    }

    public MemberView toModel(MemberEntity entity) {
        parameterRequired(entity, "Member entity");
        final var userEntity = entity.getUser();
        return MemberView.builder()
                .id(new UserId(userEntity.getId()))
                .email(new Email(userEntity.getEmail()))
                .fullName("%s %s".formatted(userEntity.getFirstName(), userEntity.getLastName()))
                .role(entity.getRole())
                .build();
    }
}
