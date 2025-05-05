package com.task.management.persistence.jpa.project;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.ProjectRepositoryPort;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.AvailableTaskStatus;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskNumberSequence;
import com.task.management.persistence.jpa.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final UserEntityDao userEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;

    @Override
    public Project save(Project project) {
        projectRequired(project);
        var projectEntity = buildProjectEntity(project);
        projectEntity.setTaskNumberSequence(new TaskNumberSequence(projectEntity));
        final var owner = userEntityDao.getReference(project.getOwnerId().value());
        projectEntity.addMember(owner, MemberRole.OWNER);
        projectEntity = projectEntityDao.save(projectEntity);
        return projectMapper.toProject(projectEntity);
    }

    @Override
    public Optional<Project> find(ProjectId id) {
        projectIdRequired(id);
        return projectEntityDao.findById(id.value()).map(projectMapper::toProject);
    }

    @Override
    public List<ProjectPreview> findProjectsByMember(UserId memberId) {
        memberIdRequired(memberId);
        return projectEntityDao.findByMemberId(memberId.value())
                .map(projectMapper::toProjectPreview)
                .toList();
    }

    @Override
    public Optional<ProjectDetails> findProjectDetails(ProjectId projectId) {
        projectIdRequired(projectId);
        return projectEntityDao.findById(projectId.value()).map(projectMapper::toProjectDetails);
    }

    @Override
    public List<TaskStatus> findAvailableTaskStatuses(ProjectId projectId) {
        projectIdRequired(projectId);
        return projectEntityDao.findAvailableTaskStatuses(projectId.value()).stream()
                .map(projectMapper::toTaskStatus)
                .collect(Collectors.toList());
    }

    private ProjectEntity buildProjectEntity(Project project) {
        return ProjectEntity.builder()
                .id(Optional.ofNullable(project.getId()).map(ProjectId::value).orElse(null))
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .title(project.getTitle())
                .description(project.getDescription())
                .availableTaskStatuses(toAvailableTaskStatuses(project.getAvailableTaskStatuses()))
                .build();
    }

    private List<AvailableTaskStatus> toAvailableTaskStatuses(List<TaskStatus> taskStatuses) {
        return taskStatuses.stream()
                .map(taskStatus -> AvailableTaskStatus.builder()
                        .name(taskStatus.name())
                        .position(taskStatus.position())
                        .build())
                .collect(Collectors.toList());
    }

    private static void memberIdRequired(UserId memberId) {
        parameterRequired(memberId, "Member id");
    }

    private static void projectIdRequired(ProjectId projectId) {
        parameterRequired(projectId, "Project id");
    }

    private static void projectRequired(Project project) {
        parameterRequired(project, "Project");
    }
}
