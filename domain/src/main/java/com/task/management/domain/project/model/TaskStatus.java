package com.task.management.domain.project.model;

import java.util.Set;

public enum TaskStatus {
    TO_DO, IN_PROGRESS, DONE;

    public static Set<TaskStatus> all() {
        return Set.of(TaskStatus.values());
    }
}
