package com.task.management.application.port.out;

import com.task.management.application.model.Task;
import com.task.management.application.model.TaskId;

import java.util.Optional;

public interface FindTaskByIdPort {
    Optional<Task> find(TaskId id);
}
