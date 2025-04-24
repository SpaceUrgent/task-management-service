package com.task.management.domain.project.port.in.command;

import com.task.management.domain.project.model.MemberId;
import com.task.management.domain.project.model.MemberRole;
import com.task.management.domain.project.model.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdateMemberRoleCommand(
        @NotNull(message = "Project id is required")
        ProjectId projectId,
        @NotNull(message = "Member id is required")
        MemberId memberId,
        MemberRole role
) {

    @Builder
    public UpdateMemberRoleCommand {
    }
}
