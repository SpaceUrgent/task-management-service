package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Task;

public interface AddTaskPort {
    Task add(Task task);
}
