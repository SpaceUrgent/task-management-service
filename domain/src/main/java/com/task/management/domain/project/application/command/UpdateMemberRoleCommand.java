package com.task.management.domain.project.application.command;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.MemberRole;
import com.task.management.domain.project.model.ProjectId;
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
