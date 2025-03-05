package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;

import java.util.Optional;

public interface FindTaskDetailsByIdPort {
    Optional<TaskDetails> findTaskDetailsById(TaskId id);
}
