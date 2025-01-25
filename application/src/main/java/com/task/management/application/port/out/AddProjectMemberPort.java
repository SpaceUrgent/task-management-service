package com.task.management.application.port.out;

import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;

public interface AddProjectMemberPort {
    void addMember(ProjectId projectId, UserId memberId);
}
