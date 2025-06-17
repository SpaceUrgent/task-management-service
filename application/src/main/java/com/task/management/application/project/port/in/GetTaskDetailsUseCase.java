package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;

public interface GetTaskDetailsUseCase {
    TaskDetails getTaskDetails(UserId actorId, TaskId taskId) throws UseCaseException;
}
