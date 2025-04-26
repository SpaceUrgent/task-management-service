package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProjectDetailsDto {
    private MemberRole role;
    private ProjectDetailsDto projectDetails;

    @Builder
    public UserProjectDetailsDto(MemberRole role, ProjectDetailsDto projectDetails) {
        this.role = role;
        this.projectDetails = projectDetails;
    }
}
