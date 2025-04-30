package com.task.management.persistence.jpa.project.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.persistence.jpa.entity.MemberEntity;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class ProjectDetailsMapper {
    public static final ProjectDetailsMapper INSTANCE = new ProjectDetailsMapper(MemberViewMapper.INSTANCE);

    private final MemberViewMapper memberViewMapper;

    private ProjectDetailsMapper(MemberViewMapper memberViewMapper) {
        this.memberViewMapper = memberViewMapper;
    }

    public ProjectDetails toModel(ProjectEntity entity) {
        parameterRequired(entity, "Entity");
        final var members = entity.getMembers();
        return ProjectDetails.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .owner(memberViewMapper.toModel(entity.getOwner()))
                .members(toMembers(members))
                .build();
    }

    private Set<MemberView> toMembers(List<MemberEntity> members) {
        return members.stream()
                .map(memberViewMapper::toModel)
                .collect(Collectors.toSet());
    }
}
