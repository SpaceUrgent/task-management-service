package com.task.management.application.dto;

public record ProjectUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
