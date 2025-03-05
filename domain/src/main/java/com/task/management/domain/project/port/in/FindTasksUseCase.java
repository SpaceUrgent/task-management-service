package com.task.management.domain.project.port.in;

import com.task.management.domain.common.Page;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.query.FindTasksQuery;

public interface FindTasksUseCase {
    Page<TaskPreview> findTasks(ProjectUserId actorId, FindTasksQuery query) throws UseCaseException;
}
