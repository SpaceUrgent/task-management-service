package com.task.management.domain.project.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Getter
@ToString
@EqualsAndHashCode
public class Project {
    private final ProjectId id;
    private final Instant createdAt;
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;
    private String title;
    private String description;
    private final ProjectUserId ownerId;

    @Builder
    public Project(ProjectId id,
                   Instant createdAt,
                   String title,
                   String description,
                   ProjectUserId ownerId) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.ownerId = parameterRequired(ownerId, "Owner id");
    }

    public void updateTitle(String title) {
        recordUpdateTime();
        this.title = notBlank(title, "Title");
    }

    public void updateDescription(String description) {
        recordUpdateTime();
        this.description = description;
    }

    public boolean isOwnedBy(ProjectUserId userId) {
        parameterRequired(userId, "User id");
        return Objects.equals(this.ownerId, userId);
    }

    private void recordUpdateTime() {
        this.updatedAt = Instant.now();
    }
}
