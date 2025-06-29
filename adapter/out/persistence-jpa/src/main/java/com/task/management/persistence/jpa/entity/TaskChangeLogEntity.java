package com.task.management.persistence.jpa.entity;

import com.task.management.domain.project.model.objectvalue.TaskProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "task_change_logs")
public class TaskChangeLogEntity extends JpaEntity<Long> {

    @Column(name = "occurred_at", updatable = false, nullable = false)
    private Instant occurredAt;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false, updatable = false)
    private UserEntity actor;

    @Column(name = "field_changed", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TaskProperty taskProperty;

    @Column(name = "old_value", updatable = false)
    private String oldValue;

    @Column(name = "new_value", updatable = false)
    private String newValue;

    protected TaskChangeLogEntity() {
    }

    @Builder
    public TaskChangeLogEntity(Instant occurredAt,
                               TaskEntity task,
                               UserEntity actor,
                               TaskProperty taskProperty,
                               String oldValue,
                               String newValue) {
        this.createdAt = Instant.now();
        this.occurredAt = parameterRequired(occurredAt, "Occured at");
        this.task = parameterRequired(task, "Task");
        this.actor = parameterRequired(actor, "Actor");
        this.taskProperty = parameterRequired(taskProperty, "Task propertt");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
