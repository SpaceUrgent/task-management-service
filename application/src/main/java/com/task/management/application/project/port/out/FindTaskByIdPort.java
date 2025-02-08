package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskId;

import java.util.Optional;

public interface FindTaskByIdPort {
    Optional<Task> find(TaskId id);
}
