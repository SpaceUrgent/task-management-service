package com.task.management.application.common;

import com.task.management.domain.common.model.objectvalue.ProjectId;
import com.task.management.domain.common.model.objectvalue.TaskId;
import com.task.management.domain.common.model.objectvalue.TaskNumber;
import com.task.management.domain.common.model.objectvalue.UserId;

import java.util.Random;

public final class TestUtils {
    private TestUtils() {
    }

    public static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    public static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }

    public static TaskNumber randomTaskNumber() {
        return new TaskNumber(randomLong());
    }

    public static UserId randomUserId() {
        return new UserId(randomLong());
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }
}
