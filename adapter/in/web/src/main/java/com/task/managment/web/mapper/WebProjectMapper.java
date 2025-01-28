package com.task.managment.web.mapper;

import com.task.management.application.model.Project;
import com.task.managment.web.dto.ProjectDto;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class WebProjectMapper {

    public ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId().value())
                .title(project.getTitle())
                .description(project.getDescription())
                .build();
    }

    public List<ProjectDto> toDtoList(List<Project> projects) {
        requireNonNull(projects, "Project list is required");
        return projects.stream().map(this::toDto).toList();
    }
}
