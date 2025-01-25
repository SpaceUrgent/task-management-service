package com.task.management.persistence.jpa;

import com.task.management.application.model.Project;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements AddProjectPort {
    private final JpaProjectRepository jpaProjectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Project add(final Project project) {
        requireNonNull(project, "Project is required");
        var projectEntity = projectMapper.toEntity(project);
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }
}
