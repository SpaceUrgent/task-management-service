package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Data
public class ProjectDetailsDto {
    private final ProjectDto project;
    private final UserDto owner;
    private final List<UserDto> members;

    @Builder
    public ProjectDetailsDto(ProjectDto project,
                             UserDto owner,
                             List<UserDto> members) {
        this.project = requireNonNull(project, "Project is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.members = requireNonNull(members, "Member list is required");
    }
}
