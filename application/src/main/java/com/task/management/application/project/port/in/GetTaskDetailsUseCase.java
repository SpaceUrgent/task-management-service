package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;

public interface GetTaskDetailsUseCase {
    TaskDetails getTaskDetails(ProjectUserId actorId, TaskId taskId) throws UseCaseException;
}
