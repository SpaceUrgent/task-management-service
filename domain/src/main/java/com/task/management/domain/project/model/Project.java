package com.task.management.domain.project.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Objects;

import static com.task.management.domain.common.Validation.parameterRequired;

@Data
public class Project {
    private final ProjectId id;
    private final Instant createdAt;
    private String title;
    private String description;
    private ProjectUser owner;

    @Builder
    public Project(ProjectId id,
                   Instant createdAt,
                   String title,
                   String description,
                   ProjectUser owner) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.title = parameterRequired(title, "Title");
        this.description = description;
        this.owner = parameterRequired(owner, "Owner");
    }

    public void setTitle(String title) {
        this.title = parameterRequired(title, "Title");
    }

    public boolean isOwner(ProjectUserId userId) {
        parameterRequired(userId, "User id");
        return Objects.equals(this.owner.id(), userId);
    }
}
