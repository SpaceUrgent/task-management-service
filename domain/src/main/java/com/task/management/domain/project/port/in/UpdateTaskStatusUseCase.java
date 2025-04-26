package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskStatus;

public interface UpdateTaskStatusUseCase {
    void updateStatus(UserId actorId, TaskId taskId, TaskStatus status) throws UseCaseException;
}
