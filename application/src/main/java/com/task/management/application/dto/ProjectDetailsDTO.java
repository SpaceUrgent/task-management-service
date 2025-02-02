package com.task.management.application.dto;

import lombok.Builder;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record ProjectDetailsDTO(
        Long id,
        String title,
        String description,
        ProjectUserDTO owner,
        List<ProjectUserDTO> members
) {

    @Builder
    public ProjectDetailsDTO {
        requireNonNull(id, "Id is required");
        requireNonNull(title, "Title is required");
        requireNonNull(owner, "Owner is required");
        requireNonNull(members, "Members is required");
    }
}
