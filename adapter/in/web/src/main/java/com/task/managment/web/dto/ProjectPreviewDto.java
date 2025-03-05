package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectPreviewDto {
    private Long id;
    private String title;
    private ProjectUserDto owner;
}
