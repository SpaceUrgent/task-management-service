package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class ProjectDto {
    private final Long id;
    private final String title;
    private final String description;

    @Builder
    public ProjectDto(Long id,
                      String title,
                      String description) {
        this.id = requireNonNull(id, "Id is required");
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
    }
}
