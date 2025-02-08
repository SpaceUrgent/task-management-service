package com.task.management.application.project.model;

import static com.task.management.application.common.Validation.parameterRequired;

public record TaskStatus(String value) {
    public TaskStatus {
        parameterRequired(value, "Task status value");
    }

    public final static TaskStatus TO_DO = new TaskStatus("TO_DO");
    public final static TaskStatus IN_PROGRESS = new TaskStatus("IN_PROGRESS");
    public final static TaskStatus DONE = new TaskStatus("DONE");
}
