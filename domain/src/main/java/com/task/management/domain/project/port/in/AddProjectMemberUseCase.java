package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;

public interface AddProjectMemberUseCase {
    void addMember(ProjectUserId actorId, ProjectId projectId, String email) throws UseCaseException;
}
