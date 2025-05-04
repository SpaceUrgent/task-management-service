package com.task.management.application.project.command;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdateMemberRoleCommand(
        @NotNull(message = "Project id is required")
        ProjectId projectId,
        @NotNull(message = "Member id is required")
        UserId memberId,
        MemberRole role
) {

    @Builder
    public UpdateMemberRoleCommand {
    }
}
