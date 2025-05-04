package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.TaskId;

public interface UpdateTaskStatusUseCase {
    void updateStatus(UserId actorId, TaskId taskId, String statusName) throws UseCaseException;
}
