package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class ProjectDetailsMapper {
    public static final ProjectDetailsMapper INSTANCE = new ProjectDetailsMapper(ProjectUserMapper.INSTANCE);

    private final ProjectUserMapper projectUserMapper;

    private ProjectDetailsMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public ProjectDetails toModel(ProjectEntity entity) {
        parameterRequired(entity, "Entity");
        return ProjectDetails.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .members(toMembers(entity.getMembers()))
                .build();
    }

    private Set<ProjectUser> toMembers(List<UserEntity> members) {
        return members.stream()
                .map(projectUserMapper::toModel)
                .collect(Collectors.toSet());
    }
}
