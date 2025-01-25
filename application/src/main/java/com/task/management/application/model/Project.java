package com.task.management.application.model;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class Project {
    private final ProjectId id;
    private String title;
    private String description;
    private UserId owner;

    @Builder
    public Project(ProjectId id,
                   String title,
                   String description,
                   UserId owner) {
        this.id = id;
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
        this.owner = requireNonNull(owner, "Owner is required");
    }
}
