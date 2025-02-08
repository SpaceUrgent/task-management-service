package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;

public interface AddProjectMemberUseCase {
    void addMember(ProjectUserId actorId, ProjectId projectId, String email) throws UseCaseException;
}
