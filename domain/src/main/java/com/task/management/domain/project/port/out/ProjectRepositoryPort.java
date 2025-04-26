package com.task.management.domain.project.port.out;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.projection.ProjectDetails;
import com.task.management.domain.project.projection.ProjectPreview;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Project save(Project project);

    Optional<Project> find(ProjectId id);

    List<ProjectPreview> findProjectsByMember(UserId memberId);

    Optional<ProjectDetails> findProjectDetails(ProjectId projectId);
}
