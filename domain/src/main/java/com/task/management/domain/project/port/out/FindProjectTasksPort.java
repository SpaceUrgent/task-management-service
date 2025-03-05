package com.task.management.domain.project.port.out;

import com.task.management.domain.common.Page;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.query.FindTasksQuery;

public interface FindProjectTasksPort {
    Page<TaskPreview> findProjectTasks(FindTasksQuery query);
}
