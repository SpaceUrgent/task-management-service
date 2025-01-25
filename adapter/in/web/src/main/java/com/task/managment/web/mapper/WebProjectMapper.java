package com.task.managment.web.mapper;

import com.task.management.application.model.Project;
import com.task.managment.web.dto.ProjectDto;

public class WebProjectMapper {

    public ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId().value())
                .title(project.getTitle())
                .description(project.getDescription())
                .build();
    }
}
