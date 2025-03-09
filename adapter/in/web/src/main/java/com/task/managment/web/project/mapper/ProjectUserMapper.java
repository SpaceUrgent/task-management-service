package com.task.managment.web.project.mapper;

import com.task.management.domain.project.model.ProjectUser;
import com.task.managment.web.project.dto.ProjectUserDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserMapper {

    public ProjectUserDto toDto(ProjectUser model) {
        return ProjectUserDto.builder()
                .id(model.id().value())
                .email(model.email().value())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .build();
    }
}
