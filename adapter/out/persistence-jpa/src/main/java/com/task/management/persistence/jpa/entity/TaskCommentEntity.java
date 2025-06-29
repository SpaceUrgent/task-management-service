package com.task.management.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "task_comments")
public class TaskCommentEntity extends JpaEntity<Long> {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private UserEntity author;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private TaskEntity task;

    @Column(columnDefinition = "text")
    private String content;

    protected TaskCommentEntity() {
    }

    @Builder
    public TaskCommentEntity(Long id,
                             Instant createdAt,
                             UserEntity author,
                             TaskEntity task,
                             String content) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.author = parameterRequired(author, "Author");
        this.task = parameterRequired(task, "Task");
        this.content = notBlank(content, "Content");
    }
}
