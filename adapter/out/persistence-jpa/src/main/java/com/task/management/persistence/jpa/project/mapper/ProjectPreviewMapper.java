package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import static java.util.Objects.requireNonNull;

public class ProjectPreviewMapper {
    public static final ProjectPreviewMapper INSTANCE = new ProjectPreviewMapper(ProjectUserMapper.INSTANCE);

    private final ProjectUserMapper projectUserMapper;

    private ProjectPreviewMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public ProjectPreview toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return ProjectPreview.builder()
                .id(new ProjectId(entity.getId()))
                .title(entity.getTitle())
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .build();
    }
}
