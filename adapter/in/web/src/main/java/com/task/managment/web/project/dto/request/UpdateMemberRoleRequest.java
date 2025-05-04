package com.task.managment.web.project.dto.request;

import com.task.management.domain.project.model.objectvalue.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    @NotNull(message = "Member id is required")
    private Long memberId;
    private MemberRole role;
}
