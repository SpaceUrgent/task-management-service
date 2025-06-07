package com.task.managment.web.project.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.common.model.objectvalue.TaskPriority;
import com.task.management.domain.common.model.objectvalue.TaskStatus;
import com.task.managment.web.project.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final MemberMapper projectUserDtoMapper;

    public ProjectPreviewDto toDto(ProjectPreview model) {
        return ProjectPreviewDto.builder()
                .id(model.id().value())
                .title(model.title())
                .owner(projectUserDtoMapper.toDto(model.owner()))
                .build();
    }

    public UserProjectDetailsDto toDto(UserId actorId, ProjectDetails projectDetails) {
        return UserProjectDetailsDto.builder()
                .role(getActorRole(actorId, projectDetails))
                .projectDetails(toDto(projectDetails))
                .build();
    }

    public ProjectDetailsDto toDto(ProjectDetails model) {
        parameterRequired(model, "Model");
        return ProjectDetailsDto.builder()
                .id(model.id().value())
                .createdAt(model.createdAt())
                .updatedAt(model.updatedAt())
                .title(model.title())
                .description(model.description())
                .taskStatuses(model.taskStatuses().stream().map(this::toDto).toList())
                .taskPriorities(model.taskPriorities().stream().map(this::toDto).toList())
                .owner(projectUserDtoMapper.toDto(model.owner()))
                .members(model.members().stream().map(projectUserDtoMapper::toDto).collect(Collectors.toSet()))
                .build();
    }

    private AvailableTaskStatusDto toDto(TaskStatus taskStatus) {
        return AvailableTaskStatusDto.builder()
                .name(taskStatus.name())
                .position(taskStatus.position())
                .build();
    }

    private TaskPriorityDto toDto(TaskPriority priority) {
        return TaskPriorityDto.builder()
                .name(priority.priorityName())
                .order(priority.order())
                .build();
    }

    private String getActorRole(UserId actorId, ProjectDetails projectDetails) {
        return projectDetails.members().stream()
                .filter(member -> Objects.equals(actorId, member.id()))
                .findFirst()
                .map(MemberView::role)
                .map(MemberRole::roleName)
                .orElse(null);
    }
}
