package com.task.management.application.service;

import com.task.management.application.model.ProjectId;
import com.task.management.application.model.TaskId;
import com.task.management.application.model.UserId;

import java.util.Objects;

public final class Validation {
    private Validation() {
    }

    public static void userIdRequired(UserId userId) {
        parameterRequired(userId, "User id");
    }

    public static void projectIdRequired(ProjectId projectId) {
        parameterRequired(projectId, "Project id");
    }

    public static void taskIdRequired(TaskId taskId) {
        parameterRequired(taskId, "Task id");
    }

    public static <T> void parameterRequired(T parameterValue, String parameterName) {
        Objects.requireNonNull(parameterValue, "%s is required".formatted(parameterName));
    }
}
