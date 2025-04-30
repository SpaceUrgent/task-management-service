package com.task.managment.web.project.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.MemberRole;
import com.task.managment.web.project.dto.ProjectDetailsDto;
import com.task.managment.web.project.dto.ProjectPreviewDto;
import com.task.managment.web.project.dto.UserProjectDetailsDto;
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
                .taskStatuses(model.taskStatuses())
                .owner(projectUserDtoMapper.toDto(model.owner()))
                .members(model.members().stream().map(projectUserDtoMapper::toDto).collect(Collectors.toSet()))
                .build();
    }

    private MemberRole getActorRole(UserId actorId, ProjectDetails projectDetails) {
        return projectDetails.members().stream()
                .filter(member -> Objects.equals(actorId, member.id()))
                .findFirst()
                .map(MemberView::role)
                .orElseThrow(() -> new IllegalArgumentException("Actor role not found"));
    }
}
