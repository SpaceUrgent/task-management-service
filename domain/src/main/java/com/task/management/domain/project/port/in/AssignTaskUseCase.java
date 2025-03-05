package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskId;

public interface AssignTaskUseCase {
    void assignTask(ProjectUserId actorId, TaskId taskId, ProjectUserId assigneeId) throws UseCaseException;
}
