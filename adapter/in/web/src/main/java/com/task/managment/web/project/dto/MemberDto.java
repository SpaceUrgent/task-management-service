package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;
    private String email;
    private String fullName;
    private String role;
}
