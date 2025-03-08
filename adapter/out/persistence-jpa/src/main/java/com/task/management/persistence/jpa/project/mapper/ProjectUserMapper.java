package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.common.Email;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.entity.UserEntity;

public class ProjectUserMapper {
    public static final ProjectUserMapper INSTANCE = new ProjectUserMapper();

    private ProjectUserMapper() {
    }

    public ProjectUser toModel(UserEntity entity) {
        return ProjectUser.builder()
                .id(new ProjectUserId(entity.getId()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(new Email(entity.getEmail()))
                .build();
    }
}
