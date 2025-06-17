package com.task.management.application.project.port.in;

import com.task.management.application.project.command.UpdateMemberRoleCommand;
import com.task.management.application.shared.UseCaseException;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

public interface ProjectMemberUseCase {
    void updateMemberRole(UserId actorId, UpdateMemberRoleCommand command) throws UseCaseException;
}
