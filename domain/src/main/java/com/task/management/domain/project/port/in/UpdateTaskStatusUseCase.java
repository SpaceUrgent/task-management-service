package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskStatus;

public interface UpdateTaskStatusUseCase {
    void updateStatus(ProjectUserId actorId, TaskId taskId, TaskStatus status) throws UseCaseException;
}
