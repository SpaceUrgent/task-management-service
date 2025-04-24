package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.MemberId;
import com.task.management.domain.project.port.in.command.UpdateMemberRoleCommand;

public interface UpdateMemberRoleUseCase {

    void updateMemberRole(MemberId actorId, UpdateMemberRoleCommand command) throws UseCaseException;
}
