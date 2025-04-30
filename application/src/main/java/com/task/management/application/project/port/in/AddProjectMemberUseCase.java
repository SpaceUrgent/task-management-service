package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;

public interface AddProjectMemberUseCase {
    void addMember(UserId actorId, ProjectId projectId, Email email) throws UseCaseException;
}
