package com.task.management.application.dto;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

public record TaskDetailsDTO(
        Long id,
        String title,
        String description,
        String status,
        ProjectUserDTO owner,
        ProjectUserDTO assignee
) {
    @Builder
    public TaskDetailsDTO {
        requireNonNull(id, "Id is required");
        requireNonNull(title, "Title is required");
        requireNonNull(description, "Description is required");
        requireNonNull(status, "Task status is required");
        requireNonNull(owner, "Owner is required");
        requireNonNull(assignee, "Assignee is required");
    }
}
