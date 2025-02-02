package com.task.management.application.model;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Data
public class Project {
    private final ProjectId id;
    private String title;
    private String description;
    private ProjectUser owner;

    @Builder
    public Project(ProjectId id,
                   String title,
                   String description,
                   ProjectUser owner) {
        this.id = id;
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
        this.owner = requireNonNull(owner, "Owner is required");
    }

    public boolean isOwner(UserId userId) {
        requireNonNull(userId, "User id is required");
        return Objects.equals(this.owner.id(), userId);
    }
}
