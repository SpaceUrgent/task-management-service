package com.task.management.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Entity
@Data
@Table(
        name = "task_statuses"
)
public class TaskStatusEntity {

    @EmbeddedId
    private TaskStatusId id;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("project")
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private ProjectEntity project;

    protected TaskStatusEntity() {
    }

    @Builder
    public TaskStatusEntity(String name,
                            Integer position,
                            ProjectEntity projectEntity) {
        this.id = new TaskStatusId(projectEntity.id, notBlank(name, "Task name"));
        this.position = parameterRequired(position, "Task position");
        this.project = projectEntity;
    }

    public String getName() {
        return Optional.ofNullable(this.id).map(TaskStatusId::getName).orElse(null);
    }
}
