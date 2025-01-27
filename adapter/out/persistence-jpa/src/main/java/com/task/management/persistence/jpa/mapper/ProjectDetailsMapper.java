package com.task.management.persistence.jpa.mapper;

import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.User;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProjectDetailsMapper {
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public ProjectDetails toModel(ProjectEntity projectEntity) {
        return ProjectDetails.builder()
                .project(projectMapper.toModel(projectEntity))
                .owner(userMapper.toModel(projectEntity.getOwner()))
                .members(toMembers(projectEntity))
                .build();
    }

    private List<User> toMembers(ProjectEntity projectEntity) {
        return projectEntity.getMembers().stream().map(userMapper::toModel).toList();
    }
}
