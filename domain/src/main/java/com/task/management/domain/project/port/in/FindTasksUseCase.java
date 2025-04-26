package com.task.management.domain.project.port.in;

import com.task.management.domain.common.projection.Page;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.management.domain.project.application.query.FindTasksQuery;

public interface FindTasksUseCase {
    Page<TaskPreview> findTasks(UserId actorId, FindTasksQuery query) throws UseCaseException;
}
