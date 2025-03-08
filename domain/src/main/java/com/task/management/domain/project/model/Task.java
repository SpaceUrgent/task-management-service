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
public class Task {
    private final TaskId id;
    private final Instant createdAt;
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;
    private final ProjectId project;
    private String title;
    private String description;
    private TaskStatus status;
    private final ProjectUserId owner;
    private ProjectUserId assignee;

    @Builder
    public Task(TaskId id,
                Instant createdAt,
                Instant updatedAt,
                ProjectId project,
                String title,
                String description,
                TaskStatus status,
                ProjectUserId owner,
                ProjectUserId assignee) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created time");
        this.updatedAt = updatedAt;
        this.project = parameterRequired(project, "Project id");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
    }

    public void updateTitle(String title) {
        recordUpdateTime();
        this.title = notBlank(title, "Title");
    }

    public void updateStatus(TaskStatus status) {
        recordUpdateTime();
        this.status = parameterRequired(status, "Status");
    }

    public void updateDescription(String description) {
        recordUpdateTime();
        this.description = description;
    }

    public void assignTo(ProjectUserId assignee) {
        recordUpdateTime();
        this.assignee = parameterRequired(assignee, "Assignee");
    }

    public boolean isOwnedBy(ProjectUserId userId) {
        return Objects.equals(this.owner, userId);
    }

    public boolean isAssignedTo(ProjectUserId userId) {
        return Objects.equals(this.assignee, userId);
    }

    private void recordUpdateTime() {
        this.updatedAt = Instant.now();
    }
}
