package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.persistence.jpa.entity.MemberEntity;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public class MemberViewMapper {

    public static MemberViewMapper INSTANCE = new MemberViewMapper();

    private MemberViewMapper() {
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
