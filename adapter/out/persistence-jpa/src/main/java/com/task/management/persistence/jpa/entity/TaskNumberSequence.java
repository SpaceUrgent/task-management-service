package com.task.management.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Entity
@Data
@Table(name = "task_number_seq")
public class TaskNumberSequence {
    @Id @Column(name = "project_id")
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(name = "current_value")
    private Long currentValue = 0L;

    protected TaskNumberSequence() {
    }

    public TaskNumberSequence(ProjectEntity project) {
        this.id = project.id;
        this.createdAt = Instant.now();
        this.project = parameterRequired(project, "Project");
    }

    public Long nextValue() {
        this.updatedAt = Instant.now();
        return ++this.currentValue;
    }
}
