package com.task.management.application.service.mapper;

import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectUser;

public class ProjectMapper {
    public ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId().value())
                .title(project.getTitle())
                .description(project.getDescription())
                .owner(toProjectUserDTO(project.getOwner()))
                .build();
    }

    private ProjectUserDTO toProjectUserDTO(ProjectUser projectUser) {
        return ProjectUserDTO.builder()
                .id(projectUser.id().value())
                .email(projectUser.email())
                .firstName(projectUser.firstName())
                .lastName(projectUser.lastName())
                .build();
    }
}
