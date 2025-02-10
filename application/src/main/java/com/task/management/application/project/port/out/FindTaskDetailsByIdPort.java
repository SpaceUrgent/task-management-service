package com.task.management.application.project.port.out;

import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;

import java.util.Optional;

public interface FindTaskDetailsByIdPort {
    Optional<TaskDetails> findTaskDetailsById(TaskId id);
}
