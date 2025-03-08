package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Project save(Project project);

    Optional<Project> find(ProjectId id);

    List<ProjectPreview> findProjectsByMember(ProjectUserId memberId);

    void addMember(ProjectId projectId, ProjectUserId memberId);

    List<ProjectUser> findMembers(ProjectId id);

    Optional<ProjectDetails> findProjectDetails(ProjectId projectId);

    boolean isMember(ProjectUserId memberId, ProjectId projectId);
}
