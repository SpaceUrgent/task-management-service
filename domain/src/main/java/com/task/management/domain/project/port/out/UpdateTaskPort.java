package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.Task;

public interface UpdateTaskPort {
    Task update(Task task);
}
