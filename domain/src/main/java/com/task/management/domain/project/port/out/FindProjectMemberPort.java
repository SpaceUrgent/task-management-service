package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.Optional;

public interface FindProjectMemberPort {
    Optional<ProjectUser> findMember(ProjectUserId memberId, ProjectId projectId);
}
