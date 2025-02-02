package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.GetProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.ProjectHasMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import com.task.management.persistence.jpa.mapper.ProjectDetailsMapper;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements AddProjectPort,
                                                    FindProjectPort,
                                                    FindProjectsByMemberPort,
                                                    GetProjectDetailsPort,
                                                    UpdateProjectPort,
                                                    AddProjectMemberPort,
                                                    ProjectHasMemberPort {
    private final JpaProjectRepository jpaProjectRepository;
    private final JpaUserRepository jpaUserRepository;
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
    public Optional<Project> findById(ProjectId id) {
        projectIdRequired(id);
        return jpaProjectRepository.findById(id.value()).map(projectMapper::toModel);
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
    public ProjectDetailsDTO getProjectDetails(ProjectId projectId) {
        projectIdRequired(projectId);
        return jpaProjectRepository.findById(projectId.value())
                .map(projectDetailsMapper::toDTO)
                .orElse(null);
    }

    @Override
    public Project update(final Project project) {
        projectRequired(project);
        var projectEntity = getProjectEntity(project.getId().value());
        projectEntity.setUpdatedAt(Instant.now());
        projectEntity.setTitle(project.getTitle());
        projectEntity.setDescription(project.getDescription());
        projectEntity.setOwner(jpaUserRepository.getReferenceById(project.getOwner().id().value()));
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public boolean hasMember(ProjectId projectId, UserId userId) {
        projectIdRequired(projectId);
        requireNonNull(userId, "User id is required");
        return jpaProjectRepository.findById(projectId.value())
                .map(ProjectEntity::getMembers)
                .orElse(new ArrayList<>()).stream()
                .anyMatch(userEntity -> userId.value().equals(userEntity.getId()));
    }

    @Override
    public void addMember(ProjectId projectId, UserId memberId) {
        projectIdRequired(projectId);
        requireNonNull(memberId, "Member id is required");
        final var projectEntity = getProjectEntity(projectId.value());
        projectEntity.addMember(jpaUserRepository.getReferenceById(memberId.value()));
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
