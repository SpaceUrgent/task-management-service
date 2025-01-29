package com.task.managment.web.mapper;

import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.managment.web.dto.ProjectDetailsDto;
import com.task.managment.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class WebProjectMapper {
    private final WebUserMapper webUserMapper;

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

    public ProjectDetailsDto toDto(ProjectDetails projectDetails) {
        requireNonNull(projectDetails, "Project details model is required");
        return ProjectDetailsDto.builder()
                .project(toDto(projectDetails.project()))
                .owner(webUserMapper.toDto(projectDetails.owner()))
                .members(webUserMapper.toDtoList(projectDetails.members()))
                .build();
    }
}
