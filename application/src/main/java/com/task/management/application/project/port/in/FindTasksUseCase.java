package com.task.management.application.project.port.in;

import com.task.management.application.common.Page;
import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.port.in.query.FindTasksQuery;

public interface FindTasksUseCase {
    Page<TaskPreview> findTasks(ProjectUserId actorId, FindTasksQuery query) throws UseCaseException;
}
