package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskId;

import java.util.Optional;

public interface FindTaskByIdPort {
    Optional<Task> find(TaskId id);
}
