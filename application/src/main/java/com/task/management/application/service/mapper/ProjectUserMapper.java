package com.task.management.application.service.mapper;

import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.model.ProjectUser;

import static java.util.Objects.requireNonNull;

public class ProjectUserMapper {

    public ProjectUserDTO toDTO(ProjectUser model) {
        requireNonNull(model, "Model is required");
        return ProjectUserDTO.builder()
                .id(model.id().value())
                .email(model.email())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .build();
    }
}
