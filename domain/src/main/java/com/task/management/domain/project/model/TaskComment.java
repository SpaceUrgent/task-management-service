package com.task.management.domain.project.model;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.TaskCommentId;
import com.task.management.domain.common.model.objectvalue.TaskId;
import lombok.*;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Getter
@ToString
@EqualsAndHashCode
public class TaskComment {
    private final TaskCommentId id;
    private final Instant createdAt;
    private final TaskId task;
    private final UserId author;
    private final String content;

    @Builder
    public TaskComment(TaskCommentId id,
                       Instant createdAt,
                       TaskId task,
                       UserId author,
                       String content) {
        this.id = id;
        this.createdAt = createdAt;
        this.task = parameterRequired(task, "Task id");
        this.author = parameterRequired(author, "Author id");
        this.content = notBlank(content, "Content");
    }
}
