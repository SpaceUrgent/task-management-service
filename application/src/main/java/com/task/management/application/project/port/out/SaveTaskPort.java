package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Task;

public interface SaveTaskPort {
    Task save(Task task);
}
