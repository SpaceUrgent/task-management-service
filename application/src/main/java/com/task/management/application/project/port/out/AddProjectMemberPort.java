package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;

public interface AddProjectMemberPort {
    void addMember(ProjectId projectId, ProjectUserId memberId);
}
