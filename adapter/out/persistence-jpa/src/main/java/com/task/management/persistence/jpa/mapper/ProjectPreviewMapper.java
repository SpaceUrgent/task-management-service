package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectPreviewMapper {
    private final ProjectUserMapper projectUserMapper;

    public ProjectPreview toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return ProjectPreview.builder()
                .id(new ProjectId(entity.getId()))
                .title(entity.getTitle())
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .build();
    }
}
