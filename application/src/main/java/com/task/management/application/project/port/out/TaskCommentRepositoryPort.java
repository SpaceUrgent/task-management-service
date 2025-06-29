package com.task.management.application.project.port.out;

import com.task.management.domain.project.model.TaskComment;

public interface TaskCommentRepositoryPort {
    TaskComment save(TaskComment comment);
}
