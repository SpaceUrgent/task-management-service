package com.task.management.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Entity
@Data
@Table(
        name = "task_statuses"
)
@IdClass(TaskStatusId.class)
public class TaskStatusEntity {
    @Id
    @Column(name = "project_id")
    private Long project;

    @Id
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, insertable = false, updatable = false)
    private ProjectEntity projectEntity;

    protected TaskStatusEntity() {
    }

    @Builder
    public TaskStatusEntity(String name,
                            Integer position,
                            ProjectEntity projectEntity) {
        this.name = notBlank(name, "Task name");
        this.position = parameterRequired(position, "Task position");
        this.project = projectEntity.getId();
        this.projectEntity = projectEntity;
    }
}
