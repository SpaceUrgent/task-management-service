package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;

public interface GetTaskDetailsUseCase {
    TaskDetails getTaskDetails(UserId actorId, TaskId taskId) throws UseCaseException;
}
