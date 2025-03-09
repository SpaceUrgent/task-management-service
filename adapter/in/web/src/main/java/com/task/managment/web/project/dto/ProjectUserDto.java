package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
