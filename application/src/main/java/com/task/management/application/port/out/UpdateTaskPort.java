package com.task.management.application.port.out;

import com.task.management.application.model.Task;

public interface UpdateTaskPort {
    Task update(Task task);
}
