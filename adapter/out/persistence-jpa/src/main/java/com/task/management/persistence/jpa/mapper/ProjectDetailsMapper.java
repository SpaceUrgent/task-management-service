package com.task.management.persistence.jpa.mapper;

import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProjectDetailsMapper {
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public ProjectDetailsDTO toDTO(ProjectEntity projectEntity) {
        return ProjectDetailsDTO.builder()
                .id(projectEntity.getId())
                .title(projectEntity.getTitle())
                .description(projectEntity.getDescription())
                .owner(toProjectUserDTO(projectEntity.getOwner()))
                .members(toProjectUserDTOSet(projectEntity.getMembers()))
                .build();
    }

    private List<ProjectUserDTO> toProjectUserDTOSet(List<UserEntity> userEntities) {
        return userEntities.stream().map(this::toProjectUserDTO).toList();
    }

    private ProjectUserDTO toProjectUserDTO(UserEntity userEntity) {
        return ProjectUserDTO.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }
}
