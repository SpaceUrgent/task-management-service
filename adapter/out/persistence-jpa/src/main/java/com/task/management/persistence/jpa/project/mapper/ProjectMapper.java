package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import static java.util.Objects.requireNonNull;

public class ProjectMapper {
    public static final ProjectMapper INSTANCE = new ProjectMapper(ProjectUserMapper.INSTANCE);

    private final ProjectUserMapper projectUserMapper;

    private ProjectMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public Project toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return Project.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .ownerId(new ProjectUserId(entity.getOwner().getId()))
                .build();
    }
}
