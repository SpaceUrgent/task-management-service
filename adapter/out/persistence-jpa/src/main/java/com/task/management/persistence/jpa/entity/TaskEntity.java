package com.task.management.persistence.jpa.entity;

import com.task.management.domain.project.model.TaskStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@NamedEntityGraph(
        name = "task-details",
        attributeNodes = {
                @NamedAttributeNode("description"),
                @NamedAttributeNode("project"),
                @NamedAttributeNode(value = "changeLogs", subgraph = "change-log-actor")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "change-log-actor",
                        attributeNodes = {
                                @NamedAttributeNode("actor")
                        }
                )
        }
)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(
        name = "tasks",
        uniqueConstraints = @UniqueConstraint(name = "task_number_constraint", columnNames = {"number", "project"})
)
public class TaskEntity extends JpaEntity<Long> {
    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(nullable = false, updatable = false)
    private Long number;

    @Column(nullable = false)
    private String title;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private UserEntity assignee;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "task"
    )
    private List<TaskChangeLogEntity> changeLogs = new ArrayList<>();

    protected TaskEntity() {
    }

    @Builder
    public TaskEntity(Long id,
                      Instant createdAt,
                      Instant updatedAt,
                      LocalDate dueDate,
                      Long number,
                      String title,
                      String description,
                      TaskStatus status,
                      UserEntity owner,
                      UserEntity assignee,
                      ProjectEntity project) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.number = parameterRequired(number, "Number");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
        this.project = parameterRequired(project, "Project");
    }

}
