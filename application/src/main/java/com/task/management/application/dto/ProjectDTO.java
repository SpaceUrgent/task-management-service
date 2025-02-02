package com.task.management.application.dto;

import lombok.Builder;

public record ProjectDTO(
        Long id,
        String title,
        String description,
        ProjectUserDTO owner
) {

    @Builder
    public ProjectDTO {
    }
}
