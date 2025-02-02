package com.task.management.application.dto;

import java.util.Set;

public record ProjectDetailsDTO(
        Long id,
        String title,
        String description,
        ProjectUserDTO owner,
        Set<ProjectUserDTO> members
) {
}
