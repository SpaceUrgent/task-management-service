package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import static java.util.Objects.requireNonNull;

public class ProjectMapper {
    public static final ProjectMapper INSTANCE = new ProjectMapper();

    public Project toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return Project.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .ownerId(new UserId(entity.getOwner().getId().getMemberId()))
                .build();
    }
}
