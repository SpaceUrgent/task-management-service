package com.task.management.application.project.port.in.query;

import com.task.management.application.common.Validation;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskStatus;
import lombok.Builder;

import java.util.List;
import java.util.Set;

import static com.task.management.application.common.Validation.parameterRequired;

public record FindTasksQuery(
        ProjectId projectId,
        Integer pageNo,
        Integer pageSize,
        Set<TaskStatus> statuses,
        ProjectUserId assignee,
        List<Sort> sortBy
) {

    @Builder
    public FindTasksQuery {
        parameterRequired(projectId, "Project id");
        parameterRequired(pageNo, "Page no");
        parameterRequired(pageSize, "Page size");
    }

    public record Sort(String field, Direction direction) {
        public Sort {
            parameterRequired(field, "Field");
            parameterRequired(direction, "Direction");
        }

        public enum Direction {ASC, DESC}
    }
}
