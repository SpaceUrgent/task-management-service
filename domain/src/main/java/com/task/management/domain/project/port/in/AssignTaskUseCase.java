package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;

public interface AssignTaskUseCase {
    void assignTask(UserId actorId, TaskId taskId, UserId assigneeId) throws UseCaseException;
}
