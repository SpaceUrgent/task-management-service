package com.task.management.application.dto;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

public record UserDTO(
        Long id,
        String email,
        String firstName,
        String lastName
) {

    @Builder
    public UserDTO {
        requireNonNull(id, "Id is required");
        requireNonNull(email, "Email is required");
        requireNonNull(firstName, "First name is required");
        requireNonNull(lastName, "Last name is required");
    }
}
