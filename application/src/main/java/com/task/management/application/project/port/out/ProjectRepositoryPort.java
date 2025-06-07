package com.task.management.application.project.port.out;

import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.common.model.objectvalue.ProjectId;
import com.task.management.domain.common.model.objectvalue.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Project save(Project project);

    Optional<Project> find(ProjectId id);

    List<ProjectPreview> findProjectsByMember(UserId memberId);

    Optional<ProjectDetails> findProjectDetails(ProjectId projectId);

    List<TaskStatus> findAvailableTaskStatuses(ProjectId projectId);
}
