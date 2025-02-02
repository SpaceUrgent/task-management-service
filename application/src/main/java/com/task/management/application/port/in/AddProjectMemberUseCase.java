package com.task.management.application.port.in;

import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;

public interface AddProjectMemberUseCase {
    void addMember(UserId currentUserId, ProjectId projectId, UserId memberId) throws InsufficientPrivilegesException, EntityNotFoundException;
}
