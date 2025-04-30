package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;

public interface AssignTaskUseCase {
    void assignTask(UserId actorId, TaskId taskId, UserId assigneeId) throws UseCaseException;
}
