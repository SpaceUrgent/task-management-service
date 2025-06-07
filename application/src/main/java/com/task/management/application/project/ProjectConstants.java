package com.task.management.application.project;

import com.task.management.domain.common.model.objectvalue.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public final class ProjectConstants {
    private ProjectConstants() {}

    public static final List<TaskStatus> DEFAULT_TASK_STATUSES = new ArrayList<>() {{
        add(TaskStatus.builder().name("To do").position(1).build());
        add(TaskStatus.builder().name("In progress").position(2).build());
        add(TaskStatus.builder().name("Done").position(3).build());
    }};
}
