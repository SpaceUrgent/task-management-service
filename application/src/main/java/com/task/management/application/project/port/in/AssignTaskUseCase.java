package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskId;

public interface AssignTaskUseCase {
    void assignTask(ProjectUserId actorId, TaskId taskId, ProjectUserId assigneeId) throws UseCaseException;
}
