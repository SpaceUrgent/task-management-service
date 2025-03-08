package com.task.management.persistence.jpa.mapper;

import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.entity.UserEntity;

public class ProjectUserMapper {
    ProjectUserMapper() {
    }

    public ProjectUser toModel(UserEntity entity) {
        return ProjectUser.builder()
                .id(new ProjectUserId(entity.getId()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .build();
    };
}
