package com.task.management.application.project.port.out;

import com.task.management.application.common.Page;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.port.in.query.FindTasksQuery;

public interface FindProjectTasksPort {
    Page<TaskPreview> findProjectTasks(FindTasksQuery query);
}
