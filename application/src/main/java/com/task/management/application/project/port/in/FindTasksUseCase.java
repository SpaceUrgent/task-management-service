package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.projection.Page;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;

public interface FindTasksUseCase {
    Page<TaskPreview> findTasks(UserId actorId, FindTasksQuery query) throws UseCaseException;
}
