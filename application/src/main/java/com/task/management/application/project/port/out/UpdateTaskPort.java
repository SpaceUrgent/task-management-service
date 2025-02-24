package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Task;

public interface UpdateTaskPort {
    Task update(Task task);
}
