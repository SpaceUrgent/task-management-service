package com.task.management.persistence.jpa;

import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.port.out.AddProjectMemberPort;
import com.task.management.application.project.port.out.AddProjectPort;
import com.task.management.application.project.port.out.FindProjectByIdPort;
import com.task.management.application.project.port.out.FindProjectsByMemberPort;
import com.task.management.application.project.port.out.UpdateProjectPort;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.mapper.Mappers;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.mapper.ProjectPreviewMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements FindProjectByIdPort,
                                                    FindProjectsByMemberPort,
                                                    AddProjectPort,
                                                    UpdateProjectPort,
                                                    AddProjectMemberPort {
    private final UserEntityDao jpaUserRepository;
    private final ProjectEntityDao jpaProjectRepository;
    private final ProjectMapper projectMapper = Mappers.projectMapper;
    private final ProjectPreviewMapper projectPreviewMapper = Mappers.projectPreviewMapper;

    @Override
    public Optional<Project> find(ProjectId id) {
        projectIdRequired(id);
        return jpaProjectRepository.findById(id.value()).map(projectMapper::toModel);
    }

    @Override
    public List<ProjectPreview> findProjectsByMember(ProjectUserId memberId) {
        memberIdRequired(memberId);
        return jpaProjectRepository.findByMemberId(memberId.value())
                .map(projectPreviewMapper::toModel)
                .toList();
    }

    @Override
    public Project add(Project project) {
        projectRequired(project);
        final var ownerReference = jpaUserRepository.getReference(project.getOwner().id().value());
        var projectEntity = ProjectEntity.builder()
                .createdAt(project.getCreatedAt())
                .title(project.getTitle())
                .description(project.getDescription())
                .owner(ownerReference)
                .members(new ArrayList<>() {{
                    add(ownerReference);
                }})
                .build();
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public Project update(Project project) {
        projectRequired(project);
        projectIdRequired(project.getId());
        var projectEntity = getProject(project.getId());
        final var ownerEntityReference = jpaUserRepository.getReference(project.getOwner().id().value());
        projectEntity.setTitle(project.getTitle());
        projectEntity.setDescription(project.getDescription());
        projectEntity.setOwner(ownerEntityReference);
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public void addMember(ProjectId projectId, ProjectUserId memberId) {
        projectIdRequired(projectId);
        memberIdRequired(memberId);
        final var projectEntity = getProject(projectId);
        final var memberEntityReference = jpaUserRepository.getReference(memberId.value());
        projectEntity.addMember(memberEntityReference);
        jpaProjectRepository.save(projectEntity);
    }

    private ProjectEntity getProject(ProjectId id) {
        return jpaProjectRepository.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d not found".formatted(id.value())));
    }

    private static void memberIdRequired(ProjectUserId memberId) {
        requireNonNull(memberId, "Member id is required");
    }

    private static void projectIdRequired(ProjectId projectId) {
        requireNonNull(projectId, "Project id is required");
    }

    private static void projectRequired(Project project) {
        requireNonNull(project, "Project entity is required");
    }
}
