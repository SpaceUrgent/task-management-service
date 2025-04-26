package com.task.managment.web.project.mapper;

import com.task.management.domain.project.projection.ProjectDetails;
import com.task.management.domain.project.projection.ProjectPreview;
import com.task.managment.web.project.dto.ProjectDetailsDto;
import com.task.managment.web.project.dto.ProjectPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final ProjectUserMapper projectUserDtoMapper;

    public ProjectPreviewDto toDto(ProjectPreview model) {
        return ProjectPreviewDto.builder()
                .id(model.id().value())
                .title(model.title())
                .owner(projectUserDtoMapper.toDto(model.owner()))
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
}
