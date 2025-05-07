package com.task.management.domain.project;

import com.task.management.domain.common.model.DomainEventAggregate;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.domain.project.model.objectvalue.TaskNumber;
import com.task.management.domain.project.model.objectvalue.TaskPriority;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Random;

public final class ProjectTestUtils {
    private ProjectTestUtils() {
    }

    public static Task randomTask() {
        return Task.builder()
                .id(randomTaskId())
                .number(randomTaskNumber())
                .createdAt(Instant.now())
                .project(randomProjectId())
                .status("In progress")
                .title("Title")
                .description("Description")
                .owner(randomUserId())
                .priority(TaskPriority.LOWEST)
                .assignee(randomUserId())
                .build();
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

    public static void assertNotEvents(DomainEventAggregate eventAggregate) {
        Assertions.assertTrue(eventAggregate.flushEvents().isEmpty());
    }
}
