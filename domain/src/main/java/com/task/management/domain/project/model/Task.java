package com.task.management.domain.project.model;

import com.task.management.domain.common.model.DomainEventAggregate;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.event.*;
import com.task.management.domain.project.model.objectvalue.*;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import static com.task.management.domain.common.validation.Validation.*;
import static java.util.Objects.*;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Task extends DomainEventAggregate {
    private final TaskId id;
    private final Instant createdAt;
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;
    private LocalDate dueDate;
    private final ProjectId project;
    private final TaskNumber number;
    private String title;
    private String description;
    private String status;
    private TaskPriority priority;
    private final UserId owner;
    private UserId assignee;

    @Builder
    public Task(TaskId id,
                Instant createdAt,
                Instant updatedAt,
                LocalDate dueDate,
                ProjectId project,
                TaskNumber number,
                String title,
                String description,
                String status,
                TaskPriority priority,
                UserId owner,
                UserId assignee) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created time");
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.project = parameterRequired(project, "Project id");
        this.number = number;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = notBlank(status, "Status");
        this.priority = requireNonNull(priority, "Priority");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
        this.validateSelf();
    }

    public void updateTitle(UserId actor, String title) {
        actorIdRequired(actor);
        if (this.title.equals(title)) return;
        recordUpdateTime();
        this.add(new TaskTitleUpdatedEvent(this.id, actor, this.title, title));
        this.title = notBlank(title, "Title");
    }

    public void updateDescription(UserId actor, String description) {
        actorIdRequired(actor);
        if (Objects.equals(this.description, description)) return;
        recordUpdateTime();
        this.add(new TaskDescriptionUpdatedEvent(this.id, actor, this.description, description));
        this.description = description;
    }

    public void updateDueDate(UserId actor, LocalDate dueDate) {
        actorIdRequired(actor);
        if (Objects.equals(this.dueDate, dueDate)) return;
        recordUpdateTime();
        this.add(new TaskDueDateUpdatedEvent(this.id, actor, this.dueDate, dueDate));
        this.dueDate = presentOrFuture(dueDate, "Due date");
    }

    public void updateStatus(UserId actor, String status) {
        actorIdRequired(actor);
        if (this.status.equals(status)) return;
        recordUpdateTime();
        this.add(new TaskStatusUpdatedEvent(this.id, actor, this.status, status));
        this.status = parameterRequired(status, "Status");
    }

    public void updatePriority(UserId actor, TaskPriority priority) {
        actorIdRequired(actor);
        if (this.priority == priority) return;
        recordUpdateTime();
        this.add(new TaskPriorityUpdatedEvent(this.id, actor, this.priority, priority));
        this.priority = parameterRequired(priority, "Priority");
    }

    public void assignTo(UserId actor, UserId assignee) {
        actorIdRequired(actor);
        if (this.assignee.equals(assignee)) return;
        recordUpdateTime();
        this.add(new TaskReassignedEvent(this.id, actor, this.assignee, assignee));
        this.assignee = parameterRequired(assignee, "Assignee");
    }

    private void recordUpdateTime() {
        this.updatedAt = Instant.now();
    }

    private void validateSelf() {
        if (nonNull(this.id) && isNull(this.number)) {
            throw new IllegalStateException("Existing task must contain number");
        }
    }
}
