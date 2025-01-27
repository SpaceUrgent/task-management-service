package com.task.management.persistence.jpa.mapper;

import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectMapper {
    private final JpaUserRepository jpaUserRepository;

    public ProjectEntity toEntity(Project project) {
        requireNonNull(project, "Project is required");
        final var ownerReference = jpaUserRepository.getReferenceById(project.getOwner().value());
        final var memberReferences = project.getMembers().stream()
                .map(UserId::value)
                .map(jpaUserRepository::getReferenceById)
                .toList();
        return ProjectEntity.builder()
                .createdAt(Instant.now())
                .title(project.getTitle())
                .description(project.getDescription())
                .owner(ownerReference)
                .members(memberReferences)
                .build();
    }

    public Project toModel(ProjectEntity projectEntity) {
        requireNonNull(projectEntity, "Project entity is required");
        return Project.builder()
                .id(new ProjectId(projectEntity.getId()))
                .title(projectEntity.getTitle())
                .description(projectEntity.getDescription())
                .owner(new UserId(projectEntity.getOwner().getId()))
                .members(toMembers(projectEntity))
                .build();
    }

    private static Set<UserId> toMembers(ProjectEntity projectEntity) {
        return projectEntity.getMembers().stream()
                .map(UserEntity::getId)
                .map(UserId::new)
                .collect(Collectors.toSet());
    }
}
