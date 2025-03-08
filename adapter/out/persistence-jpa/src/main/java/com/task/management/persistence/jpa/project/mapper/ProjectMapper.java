package com.task.management.persistence.jpa.mapper;

import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import static java.util.Objects.requireNonNull;

public class ProjectMapper {
    private final ProjectUserMapper projectUserMapper;

    ProjectMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public Project toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return Project.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .build();
    }
}
