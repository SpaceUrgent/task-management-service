package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import com.task.management.persistence.jpa.entity.MemberEntity;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskStatusEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;
import static java.util.Objects.requireNonNull;

public class ProjectMapper {
    public static final ProjectMapper INSTANCE = new ProjectMapper(MemberViewMapper.INSTANCE);

    private final MemberViewMapper memberViewMapper;

    public ProjectMapper(MemberViewMapper memberViewMapper) {
        this.memberViewMapper = memberViewMapper;
    }

    public Project toProject(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return Project.builder()
                .id(new ProjectId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .ownerId(new UserId(entity.getOwner().getId().getMemberId()))
                .availableTaskStatuses(toAvailableTaskStatuses(entity.getAvailableTaskStatuses()))
                .build();
    }

    public ProjectPreview toProjectPreview(ProjectEntity entity) {
        requireNonNull(entity, "Entity is required");
        return ProjectPreview.builder()
                .id(new ProjectId(entity.getId()))
                .title(entity.getTitle())
                .owner(memberViewMapper.toModel(entity.getOwner()))
                .build();
    }

    public ProjectDetails toProjectDetails(ProjectEntity entity) {
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
                .taskStatuses(toAvailableTaskStatuses(entity.getAvailableTaskStatuses()))
                .build();
    }

    public List<TaskStatus> toAvailableTaskStatuses(List<TaskStatusEntity> availableTaskStatuses) {
        parameterRequired(availableTaskStatuses, "Available task statuses");
        return availableTaskStatuses.stream()
                .sorted(Comparator.comparing(TaskStatusEntity::getPosition))
                .map(this::toTaskStatus)
                .collect(Collectors.toList());
    }

    public TaskStatus toTaskStatus(TaskStatusEntity availableTaskStatus) {
        parameterRequired(availableTaskStatus, "Available task status");
        return TaskStatus.builder()
                .name(availableTaskStatus.getName())
                .position(availableTaskStatus.getPosition())
                .build();
    }

    private Set<MemberView> toMembers(List<MemberEntity> members) {
        return members.stream()
                .map(memberViewMapper::toModel)
                .collect(Collectors.toSet());
    }
}
