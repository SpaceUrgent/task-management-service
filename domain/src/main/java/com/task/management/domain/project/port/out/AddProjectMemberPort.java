package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;

public interface AddProjectMemberPort {
    void addMember(ProjectId projectId, ProjectUserId memberId);
}
