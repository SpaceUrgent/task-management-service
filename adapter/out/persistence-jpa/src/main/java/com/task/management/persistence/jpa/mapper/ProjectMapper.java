package com.task.management.persistence.jpa.mapper;

import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.UserId;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectMapper {
    private final JpaUserRepository jpaUserRepository;

    public ProjectEntity toEntity(Project project) {
        requireNonNull(project, "Project is required");
        final var ownerReference = jpaUserRepository.getReferenceById(project.getOwner().id().value());
        return ProjectEntity.builder()
                .createdAt(Instant.now())
                .title(project.getTitle())
                .description(project.getDescription())
                .owner(ownerReference)
                .members(new ArrayList<>() {{
                    add(ownerReference);
                }})
                .build();
    }

    public Project toModel(ProjectEntity projectEntity) {
        requireNonNull(projectEntity, "Project entity is required");
        return Project.builder()
                .id(new ProjectId(projectEntity.getId()))
                .title(projectEntity.getTitle())
                .description(projectEntity.getDescription())
                .owner(toProjectUser(projectEntity.getOwner()))
                .build();
    }

    private static ProjectUser toProjectUser(UserEntity userEntity) {
        return ProjectUser.builder()
                .id(new UserId(userEntity.getId()))
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }
}
