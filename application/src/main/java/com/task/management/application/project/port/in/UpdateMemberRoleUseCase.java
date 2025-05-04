package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.UpdateMemberRoleCommand;
import com.task.management.domain.common.model.objectvalue.UserId;

public interface UpdateMemberRoleUseCase {

    void updateMemberRole(UserId actorId, UpdateMemberRoleCommand command) throws UseCaseException;
}
