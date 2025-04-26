package com.task.management.persistence.jpa.project;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.MemberRole;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.projection.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.ProjectPreview;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskNumberSequence;
import com.task.management.persistence.jpa.project.mapper.ProjectDetailsMapper;
import com.task.management.persistence.jpa.project.mapper.ProjectMapper;
import com.task.management.persistence.jpa.project.mapper.ProjectPreviewMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final UserEntityDao userEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;
    private final ProjectPreviewMapper projectPreviewMapper = ProjectPreviewMapper.INSTANCE;
    private final ProjectDetailsMapper projectDetailsMapper = ProjectDetailsMapper.INSTANCE;

    @Override
    public Project save(Project project) {
        projectRequired(project);
        var projectEntity = buildProjectEntity(project);
        projectEntity.setTaskNumberSequence(new TaskNumberSequence(projectEntity));
        final var owner = userEntityDao.getReference(project.getOwnerId().value());
        projectEntity.addMember(owner, MemberRole.OWNER);
        projectEntity = projectEntityDao.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public Optional<Project> find(ProjectId id) {
        projectIdRequired(id);
        return projectEntityDao.findById(id.value()).map(projectMapper::toModel);
    }

    @Override
    public List<ProjectPreview> findProjectsByMember(UserId memberId) {
        memberIdRequired(memberId);
        return projectEntityDao.findByMemberId(memberId.value())
                .map(projectPreviewMapper::toModel)
                .toList();
    }

    @Override
    public Optional<ProjectDetails> findProjectDetails(ProjectId projectId) {
        projectIdRequired(projectId);
        return projectEntityDao.findById(projectId.value()).map(projectDetailsMapper::toModel);
    }

    private ProjectEntity buildProjectEntity(Project project) {
        return ProjectEntity.builder()
                .id(Optional.ofNullable(project.getId()).map(ProjectId::value).orElse(null))
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .title(project.getTitle())
                .description(project.getDescription())
                .build();
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
