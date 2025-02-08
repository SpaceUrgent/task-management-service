package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;

import java.util.Optional;

public interface FindProjectMemberPort {
    Optional<ProjectUser> findMember(ProjectUserId memberId, ProjectId projectId);
}
