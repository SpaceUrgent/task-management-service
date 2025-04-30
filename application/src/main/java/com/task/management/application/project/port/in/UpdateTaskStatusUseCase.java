package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskStatus;

public interface UpdateTaskStatusUseCase {
    void updateStatus(UserId actorId, TaskId taskId, TaskStatus status) throws UseCaseException;
}
