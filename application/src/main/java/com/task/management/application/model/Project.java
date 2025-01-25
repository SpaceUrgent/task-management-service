package com.task.management.application.model;

import lombok.Builder;
import lombok.Data;

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
    private UserId owner;
    private Set<UserId> members;

    @Builder
    public Project(ProjectId id,
                   String title,
                   String description,
                   UserId owner,
                   Set<UserId> members) {
        this.id = id;
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.members = Optional.ofNullable(members).orElse(new HashSet<>());
    }

    public boolean hasMember(UserId userId) {
        return isOwner(userId) || members.contains(userId);
    }

    public boolean isOwner(UserId userId) {
        return Objects.equals(this.owner, userId);
    }
}
