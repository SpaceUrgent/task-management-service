package com.task.management.application.project.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Data
public class Task {
    private final TaskId id;
    private final Instant createdTime;
    private final ProjectId project;
    private String title;
    private String description;
    private TaskStatus status;
    private ProjectUser owner;
    private ProjectUser assignee;

    @Builder
    public Task(TaskId id,
                Instant createdTime,
                ProjectId project,
                String title,
                String description,
                TaskStatus status,
                ProjectUser owner,
                ProjectUser assignee) {
        this.id = id;
        this.createdTime = requireNonNull(createdTime, "Created time is required");
        this.project = requireNonNull(project, "Project id is required");
        this.title = requireNonNull(title, "Title is required");
        this.description = description;
        this.status = requireNonNull(status, "Status is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.assignee = requireNonNull(assignee, "Assignee is required");
    }

    public boolean isOwner(ProjectUserId userId) {
        return Objects.equals(this.owner.id(), userId);
    }

    public boolean isAssignee(ProjectUserId userId) {
        return Objects.equals(this.assignee.id(), userId);
    }
}
