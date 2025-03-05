package com.task.management.domain.project.service;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskId;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public final class ProjectTestUtils {
    private ProjectTestUtils() {
    }

    static Answer<ProjectUser> getProjectUserAnswer() {
        return invocation -> {
            final var id = (ProjectUserId) invocation.getArgument(0);
            return ProjectUser.withId(id);
        };
    }

    static List<ProjectUser> randomProjectUsers() {
        return IntStream.range(0, 10)
                .mapToObj(value -> randomProjectUser())
                .toList();
    }

    static <T> Answer<T> self(Class<T> selfClass) {
        return invocation ->  selfClass.cast(invocation.getArgument(0));
    }

    static ProjectUser randomProjectUser() {
        return ProjectUser.builder()
                .id(randomProjectUserId())
                .email("project-user@mail.com")
                .firstName("FName")
                .lastName("LName")
                .build();
    }

    static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }

    static ProjectUserId randomProjectUserId() {
        return new ProjectUserId(randomLong());
    }

    private static Long randomLong() {
        return new Random().nextLong();
    }
}
