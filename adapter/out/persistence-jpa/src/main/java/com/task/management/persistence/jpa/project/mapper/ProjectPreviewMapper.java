package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.ProjectPreview;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import static java.util.Objects.requireNonNull;

public class ProjectPreviewMapper {
    public static final ProjectPreviewMapper INSTANCE = new ProjectPreviewMapper(MemberViewMapper.INSTANCE);

    private final MemberViewMapper memberViewMapper;

    private ProjectPreviewMapper(MemberViewMapper memberViewMapper) {
        this.memberViewMapper = memberViewMapper;
    }

    public ProjectPreview toModel(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return ProjectPreview.builder()
                .id(new ProjectId(entity.getId()))
                .title(entity.getTitle())
                .owner(memberViewMapper.toModel(entity.getOwner()))
                .build();
    }
}
