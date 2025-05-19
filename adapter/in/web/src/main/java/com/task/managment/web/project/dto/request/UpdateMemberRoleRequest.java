package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    @NotNull(message = "Member id is required")
    private Long memberId;
    private String role;
}
