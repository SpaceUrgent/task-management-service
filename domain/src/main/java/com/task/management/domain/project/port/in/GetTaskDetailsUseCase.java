package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;

public interface GetTaskDetailsUseCase {
    TaskDetails getTaskDetails(ProjectUserId actorId, TaskId taskId) throws UseCaseException;
}
