package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.application.command.UpdateMemberRoleCommand;

public interface UpdateMemberRoleUseCase {

    void updateMemberRole(UserId actorId, UpdateMemberRoleCommand command) throws UseCaseException;
}
