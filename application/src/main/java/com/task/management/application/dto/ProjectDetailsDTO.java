package com.task.management.application.dto;

import lombok.Builder;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public record ProjectDetailsDTO(
        Long id,
        String title,
        String description,
        ProjectUserDTO owner,
        Set<ProjectUserDTO> members
) {

    @Builder
    public ProjectDetailsDTO {
        requireNonNull(id, "Id is required");
        requireNonNull(title, "Title is required");
        requireNonNull(owner, "Owner is required");
        requireNonNull(members, "Members is required");
    }
}
