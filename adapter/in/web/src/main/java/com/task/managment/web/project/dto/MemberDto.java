package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.objectvalue.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;
    private String email;
    private String fullName;
    private MemberRole role;
}
