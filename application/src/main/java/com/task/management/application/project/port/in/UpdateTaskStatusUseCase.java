package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskStatus;

public interface UpdateTaskStatusUseCase {
    void updateStatus(ProjectUserId actorId, TaskId taskId, TaskStatus status) throws UseCaseException;
}
