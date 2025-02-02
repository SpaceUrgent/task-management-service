package com.task.management.application.dto;

import lombok.Builder;

public record ProjectUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName
) {

    @Builder
    public ProjectUserDTO {
    }
}
