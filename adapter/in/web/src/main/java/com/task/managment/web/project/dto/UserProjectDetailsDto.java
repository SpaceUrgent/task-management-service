package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProjectDetailsDto {
    private String role;
    private ProjectDetailsDto projectDetails;

    @Builder
    public UserProjectDetailsDto(String role, ProjectDetailsDto projectDetails) {
        this.role = role;
        this.projectDetails = projectDetails;
    }
}
