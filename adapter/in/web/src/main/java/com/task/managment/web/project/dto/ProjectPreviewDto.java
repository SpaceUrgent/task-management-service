package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectPreviewDto {
    private Long id;
    private String title;
    private MemberDto owner;
}
