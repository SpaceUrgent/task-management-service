package com.task.managment.web.mapper;

import com.task.management.domain.project.model.ProjectUser;
import com.task.managment.web.dto.ProjectUserDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserDtoMapper {

    public ProjectUserDto toDto(ProjectUser projectUser) {
        return ProjectUserDto.builder()
                .id(projectUser.id().value())
                .email(projectUser.email())
                .firstName(projectUser.firstName())
                .lastName(projectUser.lastName())
                .build();
    }
}
