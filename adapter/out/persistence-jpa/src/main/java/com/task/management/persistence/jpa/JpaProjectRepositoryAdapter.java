package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectDetailsPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.mapper.ProjectDetailsMapper;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements AddProjectPort,
                                                    FindProjectsByMemberPort,
                                                    FindProjectDetailsPort,
                                                    UpdateProjectPort {
    private final JpaProjectRepository jpaProjectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectDetailsMapper projectDetailsMapper;

    @Override
    public Project add(final Project project) {
        projectRequired(project);
        var projectEntity = projectMapper.toEntity(project);
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public List<Project> findProjectsByMember(UserId member, PageQuery page) {
        requireNonNull(member, "Member id is required");
        requireNonNull(page, "Page is required");
        return jpaProjectRepository.findByMember(member.value(), JpaPage.of(page)).get()
                .map(projectMapper::toModel)
                .toList();
    }

    @Override
    public Optional<ProjectDetails> findProjectDetails(ProjectId projectId) {
        projectIdRequired(projectId);
        return jpaProjectRepository.findById(projectId.value()).map(projectDetailsMapper::toModel);
    }


    @Override
    public Project update(ProjectId id, UpdateProjectCommand command) {
        projectIdRequired(id);
        requireNonNull(command, "Update project command is required");
        final var projectEntity = getProjectEntity(id.value());
        Optional.ofNullable(command.title()).ifPresent(projectEntity::setTitle);
        Optional.ofNullable(command.description()).ifPresent(projectEntity::setDescription);
        return projectMapper.toModel(jpaProjectRepository.save(projectEntity));
    }

    private ProjectEntity getProjectEntity(final Long id) {
        requireNonNull(id, "Project id is required");
        return jpaProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project entity with id '%d' not found".formatted(id)));
    }

    private static void projectIdRequired(ProjectId projectId) {
        requireNonNull(projectId, "Project id is required");
    }

    private static void projectRequired(Project project) {
        requireNonNull(project, "Project is required");
    }
}
