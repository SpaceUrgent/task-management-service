package com.task.management.persistence.jpa.project;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskNumberSequence;
import com.task.management.persistence.jpa.project.mapper.ProjectDetailsMapper;
import com.task.management.persistence.jpa.project.mapper.ProjectMapper;
import com.task.management.persistence.jpa.project.mapper.ProjectPreviewMapper;
import com.task.management.persistence.jpa.project.mapper.ProjectUserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final UserEntityDao userEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final ProjectUserMapper projectUserMapper = ProjectUserMapper.INSTANCE;
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;
    private final ProjectPreviewMapper projectPreviewMapper = ProjectPreviewMapper.INSTANCE;
    private final ProjectDetailsMapper projectDetailsMapper = ProjectDetailsMapper.INSTANCE;

    @Override
    public Project save(Project project) {
        projectRequired(project);
        var projectEntity = buildProjectEntity(project);
        projectEntity.setTaskNumberSequence(new TaskNumberSequence(projectEntity));
        projectEntity = projectEntityDao.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public Optional<Project> find(ProjectId id) {
        projectIdRequired(id);
        return projectEntityDao.findById(id.value()).map(projectMapper::toModel);
    }

    @Override
    public List<ProjectPreview> findProjectsByMember(ProjectUserId memberId) {
        memberIdRequired(memberId);
        return projectEntityDao.findByMemberId(memberId.value())
                .map(projectPreviewMapper::toModel)
                .toList();
    }

    @Override
    public void addMember(ProjectId projectId, ProjectUserId memberId) {
        projectIdRequired(projectId);
        memberIdRequired(memberId);
        final var projectEntity = getProject(projectId);
        final var memberEntityReference = userEntityDao.getReference(memberId.value());
        projectEntity.addMember(memberEntityReference);
        projectEntityDao.save(projectEntity);
    }

    @Override
    public Optional<ProjectDetails> findProjectDetails(ProjectId projectId) {
        projectIdRequired(projectId);
        return projectEntityDao.findById(projectId.value()).map(projectDetailsMapper::toModel);
    }

    @Override
    public List<ProjectUser> findMembers(ProjectId id) {
        projectIdRequired(id);
        return userEntityDao.findByProject(id.value()).stream()
                .map(projectUserMapper::toModel)
                .toList();
    }

    @Override
    public boolean isMember(ProjectUserId memberId, ProjectId projectId) {
        memberIdRequired(memberId);
        projectIdRequired(projectId);
        return userEntityDao.isMember(memberId.value(), projectId.value());
    }

    private ProjectEntity buildProjectEntity(Project project) {
        final var ownerReference = userEntityDao.getReference(project.getOwnerId().value());
        return ProjectEntity.builder()
                .id(Optional.ofNullable(project.getId()).map(ProjectId::value).orElse(null))
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .title(project.getTitle())
                .description(project.getDescription())
                .owner(ownerReference)
                .members(new ArrayList<>() {{
                    add(ownerReference);
                }})
                .build();
    }

    private ProjectEntity getProject(ProjectId id) {
        return projectEntityDao.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d not found".formatted(id.value())));
    }

    private static void memberIdRequired(ProjectUserId memberId) {
        parameterRequired(memberId, "Member id");
    }

    private static void projectIdRequired(ProjectId projectId) {
        parameterRequired(projectId, "Project id");
    }

    private static void projectRequired(Project project) {
        parameterRequired(project, "Project");
    }
}
