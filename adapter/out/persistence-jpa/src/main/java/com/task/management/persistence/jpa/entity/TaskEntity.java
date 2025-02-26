package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "tasks")
public class TaskEntity extends JpaEntity<Long> {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private UserEntity assignee;


    protected TaskEntity() {
    }

    @Builder
    public TaskEntity(Long id,
                      Instant createdAt,
                      String title,
                      String description,
                      String status,
                      UserEntity owner,
                      UserEntity assignee,
                      ProjectEntity project) {
        this.id = id;
        this.createdAt = requireNonNull(createdAt, "Created at is required");
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
        this.status = requireNonNull(status, "Status is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.assignee = requireNonNull(assignee, "Assignee is required");
        this.project = requireNonNull(project, "Project is required");
    }

}
