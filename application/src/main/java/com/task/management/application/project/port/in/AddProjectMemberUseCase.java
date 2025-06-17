package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

public interface AddProjectMemberUseCase {
    void addMember(UserId actorId, ProjectId projectId, Email email) throws UseCaseException;
}
